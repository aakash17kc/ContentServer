package com.aakash.contentserver.controller;

import com.aakash.contentserver.dto.PostDTO;
import com.aakash.contentserver.exceptions.BadRequestException;
import com.aakash.contentserver.exceptions.ContentServerException;
import com.aakash.contentserver.processors.ImageProcessor;
import com.aakash.contentserver.services.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

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
   * @param file    Image file.
   * @param caption Caption for the post.
   * @param creator Creator of the post.
   * @return PostDTO.
   * @throws ContentServerException ContentServerException.
   */
  @PostMapping(consumes = {"multipart/form-data"})
  public ResponseEntity<PostDTO> createPost(@RequestParam("file") MultipartFile file, @RequestParam("caption") String caption,
                                            @RequestParam("creator") String creator) throws ContentServerException {
    if (file.isEmpty()) {
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
   * Method to get all created posts.
   *
   * @param page Page number.
   * @param size Number of posts to fetch.
   * @return PagedModel of PostDTO.
   */
  @GetMapping("/all")
  public ResponseEntity<PagedModel<EntityModel<PostDTO>>> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    Page<PostDTO> allPosts = postService.getAllPosts(pageable);
    PagedModel<EntityModel<PostDTO>> pagedModel = pagedResourcesAssembler.toModel(allPosts);
    
    pagedModel.add(
        WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(PostController.class)
                    .getAllPosts(page + 1, size))
            .withRel("next")
    );
    if (page > 0) {
      pagedModel.add(
          WebMvcLinkBuilder.linkTo(
                  WebMvcLinkBuilder.methodOn(PostController.class)
                      .getAllPosts(page - 1, size))
              .withRel("previous")
      );
    }
    return ResponseEntity.ok()
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
}
