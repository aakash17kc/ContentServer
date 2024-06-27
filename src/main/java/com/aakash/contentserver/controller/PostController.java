package com.aakash.contentserver.controller;

import com.aakash.contentserver.dto.PostDTO;
import com.aakash.contentserver.exceptions.BadRequestException;
import com.aakash.contentserver.exceptions.ContentServerException;
import com.aakash.contentserver.exceptions.MultipleFilesUploadException;
import com.aakash.contentserver.processors.ImageProcessor;
import com.aakash.contentserver.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.aakash.contentserver.constants.ImageConstants.SUPPORTED_IMAGES_COUNT;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * PostController class. This class can be used to handle post entity requests.
 */
@RestController
@RequestMapping("/v1/posts")
public class PostController {
  
  private final PostService postService;
  private final PagedResourcesAssembler<PostDTO> pagedResourcesAssembler;
  private final ImageProcessor imageProcessor;
  
  public PostController(PostService postService, PagedResourcesAssembler<PostDTO> pagedResourcesAssembler,
                        ImageProcessor imageProcessor) {
    this.postService = postService;
    this.pagedResourcesAssembler = pagedResourcesAssembler;
    this.imageProcessor = imageProcessor;
    
  }
  
  /**
   * Controller to create a post with image,caption and creator.The immediate response returned doesn't have the iamge url.
   * The Location header in response will have the url to access the entity.
   * Do a GET request on the postId to get the image url in the PostDTO
   *
   * @param multipartFileList List of files.
   * @param caption           Caption for the post.
   * @param creator           Creator of the post.
   * @return PostDTO.
   * @throws ContentServerException ContentServerException.
   */
  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<PostDTO> createPost(@RequestParam("file") List<MultipartFile> multipartFileList, @RequestParam("caption") String caption,
                                            @RequestParam("creator") String creator) throws ContentServerException {
    if (multipartFileList.size() > SUPPORTED_IMAGES_COUNT) {
      throw new MultipleFilesUploadException("Only one file can be uploaded for a post.");
    }
    MultipartFile file = multipartFileList.get(0);
    
    if (file.isEmpty() || file.getSize() == 0) {
      throw new BadRequestException("Selected image file is empty/invalid");
    }
    PostDTO postDTO;
    imageProcessor.validateSupportedImageType(file);
    postDTO = postService.savePost(caption, creator, file);
    
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(postDTO.getId())
        .toUri();
    return ResponseEntity
        .created(location)
        .body(postDTO);
  }
  
  /**
   * Controller to get a post based on the postId.
   *
   * @param postId PostId.
   * @return PostDTO.
   */
  @GetMapping("/{postId}")
  public ResponseEntity<PostDTO> getPost(@PathVariable String postId) {
    PostDTO savedPost = postService.getPost(postId);
    return ResponseEntity
        .ok()
        .body(savedPost);
  }
  
  @GetMapping("/next-posts")
  public ResponseEntity<CollectionModel<EntityModel<PostDTO>>> getNextPosts(@RequestParam(required = false) Long commentsCount,
                                                                            @RequestParam(defaultValue = "10") int pageSize) {
    if (commentsCount == null) {
      commentsCount = Long.MAX_VALUE;
    }
    
    List<PostDTO> posts = postService.getNextPostsByCursor(commentsCount, pageSize);
    
    Long nextCommentsCount = null;
    Long prevCommentsCount = null;
    
    if (!posts.isEmpty()) {
      PostDTO lastPost = posts.get(posts.size() - 1);
      nextCommentsCount = lastPost.getCommentsCount();
      
      PostDTO firstPost = posts.get(0);
      prevCommentsCount = firstPost.getCommentsCount();
    }
    Long finalCommentsCount = commentsCount;
    List<EntityModel<PostDTO>> postResources = posts.stream()
        .map(post -> EntityModel.of(post,
            WebMvcLinkBuilder.linkTo(methodOn(PostController.class).getNextPosts(
                finalCommentsCount, pageSize)).withSelfRel()))
        .collect(Collectors.toList());
    
    return getCollectionModelResponseEntity(pageSize, nextCommentsCount, prevCommentsCount, postResources);
  }
  
  
  @GetMapping("/prev-posts")
  public ResponseEntity<CollectionModel<EntityModel<PostDTO>>> getPreviousPosts(
      @RequestParam(required = false) Long commentsCount,
      @RequestParam(defaultValue = "10") int pageSize) {
    
    if (commentsCount == null) {
      commentsCount = Long.MAX_VALUE;
    }
    
    List<PostDTO> posts = postService.getPreviousPostsByCursor(commentsCount, pageSize);
    
    Long nextCommentsCount = null;
    Long prevCommentsCount = null;
    
    if (!posts.isEmpty()) {
      PostDTO lastPost = posts.get(posts.size() - 1);
      nextCommentsCount = lastPost.getCommentsCount();
      
      PostDTO firstPost = posts.get(0);
      prevCommentsCount = firstPost.getCommentsCount();
    }
    
    Long finalCommentsCount = commentsCount;
    List<EntityModel<PostDTO>> postResources = posts.stream()
        .map(post -> EntityModel.of(post,
            WebMvcLinkBuilder.linkTo(methodOn(PostController.class).getPreviousPosts(
                finalCommentsCount, pageSize)).withSelfRel()))
        .collect(Collectors.toList());
    
    return getCollectionModelResponseEntity(pageSize, nextCommentsCount, prevCommentsCount, postResources);
  }
  
  private ResponseEntity<CollectionModel<EntityModel<PostDTO>>> getCollectionModelResponseEntity(
      @RequestParam(defaultValue = "10") int pageSize, Long nextCommentsCount, Long prevCommentsCount,
      List<EntityModel<PostDTO>> postResources) {
    CollectionModel<EntityModel<PostDTO>> collectionModel = CollectionModel.of(postResources);
    
    if (nextCommentsCount != null) {
      Link nextLink = WebMvcLinkBuilder.linkTo(methodOn(PostController.class)
          .getNextPosts(nextCommentsCount, pageSize)).withRel("next");
      collectionModel.add(nextLink);
    }
    
    if (prevCommentsCount != null) {
      Link prevLink = WebMvcLinkBuilder.linkTo(methodOn(PostController.class)
          .getPreviousPosts(prevCommentsCount, pageSize)).withRel("prev");
      collectionModel.add(prevLink);
    }
    
    return ResponseEntity.ok(collectionModel);
  }
  
  /**
   * Controller to update the caption of a post.
   *
   * @param postId  PostId.
   * @param caption Caption.
   * @return PostDTO.
   */
  @PutMapping("/{postId}")
  public ResponseEntity<PostDTO> updatePost(@PathVariable String postId, @RequestParam("caption") String caption) {
    PostDTO updatedPost = postService.updatePost(UUID.fromString(postId), caption);
    return ResponseEntity
        .ok()
        .body(updatedPost);
  }
  
  /**
   * Controller to get the top posts based on the number of comments.
   *
   * @param page Page number.
   * @param size Number of posts to fetch.
   * @return PagedModel of PostDTO.
   */
  @GetMapping
  public ResponseEntity<PagedModel<EntityModel<PostDTO>>> getTopPosts(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<PostDTO> allPosts = postService.getTopPosts(pageable);
    PagedModel<EntityModel<PostDTO>> pagedModel = pagedResourcesAssembler.toModel(allPosts);
    
    pagedModel.add(
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PostController.class)
                    .getTopPosts(page + 1, size))
            .withRel("next")
    );
    if (page > 0) {
      pagedModel.add(
          WebMvcLinkBuilder.linkTo(
                  WebMvcLinkBuilder.methodOn(PostController.class)
                      .getTopPosts(page - 1, size))
              .withRel("previous")
      );
    }
    return ResponseEntity
        .ok()
        .body(pagedModel);
  }
  
  /**
   * Controller to get all comments for a post.
   *
   * @param postId PostId.
   * @return PostDTO.
   */
  @GetMapping("/{postId}/comments")
  public ResponseEntity<PostDTO> getAllCommentsForAPost(@PathVariable String postId) {
    PostDTO savedPost = postService.getAllCommentsForAPost(postId);
    return ResponseEntity
        .ok()
        .body(savedPost);
  }
  
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable String postId) {
    postService.deletePost(postId);
    return ResponseEntity
        .noContent()
        .build();
  }
}
