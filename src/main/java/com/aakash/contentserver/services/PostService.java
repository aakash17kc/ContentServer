package com.aakash.contentserver.services;

import com.aakash.contentserver.configuration.CircuitBreakerConfiguration;
import com.aakash.contentserver.constants.ImageConstants;
import com.aakash.contentserver.constants.S3Constants;
import com.aakash.contentserver.dto.CommentDTO;
import com.aakash.contentserver.dto.ImageDTO;
import com.aakash.contentserver.dto.PostDTO;
import com.aakash.contentserver.entities.Comment;
import com.aakash.contentserver.entities.Image;
import com.aakash.contentserver.entities.Post;
import com.aakash.contentserver.enums.ActivityType;
import com.aakash.contentserver.enums.ImageType;
import com.aakash.contentserver.exceptions.*;
import com.aakash.contentserver.processors.ImageProcessor;
import com.aakash.contentserver.repositories.CommentsRepository;
import com.aakash.contentserver.repositories.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.*;

import static com.aakash.contentserver.constants.CommonConstants.NUMBER_OF_COMMENTS_PER_POST;

/**
 * Service class to handle CRUD operations for Post entity.
 */
@Service
public class PostService extends ContentService<PostDTO> {

  private final Logger logger;
  private final ImageProcessor imageProcessor;
  private final ImageService imageService;

  private final CommentService commentService;

  public PostService(PostRepository postRepository, ModelMapper modelMapper, MongoTemplate mongoTemplate,
                     ObjectMapper objectMapper, Clock clock, CommentsRepository commentsRepository, Validator validator,
                     ImageProcessor imageProcessor, ImageService imageService, CircuitBreakerConfiguration circuitBreakerConfig, @Lazy CommentService commentService) {
    super(circuitBreakerConfig, modelMapper, mongoTemplate, objectMapper, clock, commentsRepository, postRepository, validator);
    this.imageProcessor = imageProcessor;
    this.imageService = imageService;
    this.commentService = commentService;
    logger = LoggerFactory.getLogger(PostService.class);
  }

  /**
   * Method to save a post with an image.
   *
   * @param caption      Caption for the post
   * @param creator      Creator of the post
   * @param uploadedFile Image file
   * @return PostDTO the saved post
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")
  public PostDTO savePost(String caption, String creator, MultipartFile uploadedFile) {
    Post post = new Post();
    post.setContent(caption);
    post.setCreator(creator);
    logger.info("Validating post entity");
    validateEntity(post);
    try {
      post.setId(UUID.randomUUID());
      post.setCreatedAt(Instant.now(clock));
      post.setCommentsCount(0L);
      Post savedPost = postRepository.save(post);
      logger.info("Post saved successfully with id: " + savedPost.getId());
      processImageUploadForPost(savedPost.getId(), uploadedFile, uploadedFile.getSize(), ActivityType.POST);
      return getPostToDTO(savedPost);
    } catch (Exception e) {
      throw new ContentServerException(e.getMessage(), e);
    }
  }

  /**
   * Method to process the image upload for a post. This methods uploads the original and resized image to S3.
   * Original images are uploaded to "original" folder and resized images are uploaded to "resized' folder.
   * To associate the image with the post, the an entity for the image is created which contains the post id.
   * Once the image is uploaded to S3, the image entity is saved to the database and the associated post entity is updated
   * with the image url.
   *
   * @param postId       The id of the post
   * @param file         The image file
   * @param fileSize     The size of the image file
   * @param activityType The type of activity for which the image is being uploaded
   */
  private void processImageUploadForPost(UUID postId, MultipartFile file, long fileSize, ActivityType activityType) {
    Image image = new Image();
    image.setId(UUID.randomUUID());
    image.setSizeInKB(fileSize / 1000);
    image.setCreatedAt(Instant.now(clock));
    image.setPostId(postId);
    image.setBucketName(S3Constants.BUCKET_NAME);
    image.setType(ImageType.JPG.getValue());
    String destinationFileName = ImageConstants.COMPRESSED_LOCATION + "/compressed-" +
        image.getPostId() + "." + ImageType.JPG.getValue().toLowerCase();
    image.setBucketName(S3Constants.BUCKET_NAME);
    image.setLocation(destinationFileName);
    image.setAccessUri(ImageConstants.ACCESS_URI + image.getId() + ImageConstants.CONTENT_ENDPOINT);
    try {
      imageProcessor.uploadOriginalImageToS3(file, image, activityType);
      imageProcessor.resizeImageAndUploadToS3(file, image, activityType);

      ImageDTO savedImage = imageService.saveImage(image);
      logger.info("Image saved to db successfully for postId: " + postId);

      updateImageInPost(postId, UUID.fromString(savedImage.getId()), image.getAccessUri());
      logger.info(String.format("Post updated with imageId %s for postId: %s", savedImage.getId(), postId));
    } catch (IOException e) {
      logger.error("Error while processing image for postId: " + postId, e);
      throw new ImageProcessingException(String.format("Error while processing image for postId %s", postId), e);
    } catch (Exception e) {
      logger.error("Error while saving image for postId: " + postId, e);
      throw new ContentServerException(e.getMessage(), e);
    }
  }

  /**
   * Method to get a post by id.
   *
   * @param postId The id of the post to be fetched.
   * @return PostDTO The fetched post.
   * @throws BadRequestException If the post doesn't exist.
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")
  public PostDTO getPost(String postId) throws BadRequestException {
    Optional<Post> fetchedPost;
    try {
      fetchedPost = postRepository.findById(UUID.fromString(postId));
      logger.info("Fetched post with id: " + postId);
    } catch (Exception e) {
      throw new EntityNotFoundException("Post doesn't exist with id " + postId, e);
    }
    if (fetchedPost.isPresent()) {
      return getPostToDTO(fetchedPost.get());
    } else {
      String errorMessage = "Post with id: " + postId + " doesn't exist.";
      logger.error(errorMessage);
      throw new BadRequestException(errorMessage);
    }
  }

  /**
   * Method to update a post by id. Only the caption of the post can be updated for now.
   *
   * @param postId  The id of the post to be updated.
   * @param caption The updated caption.
   * @return PostDTO The updated post.
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")
  public PostDTO updatePost(UUID postId, String caption) {
    Optional<Post> fetchedPost;
    try {
      fetchedPost = postRepository.findById(postId);
      logger.info("Fetched post with id: " + postId);
    } catch (Exception e) {
      throw new EntityNotFoundException(e.getMessage(), e);
    }
    if (fetchedPost.isPresent()) {
      Post post = fetchedPost.get();
      if (StringUtils.isNotBlank(caption)) post.setContent(caption);
      return getUpdatedPostDTO(post);
    } else {
      String errorMessage = "Post with id: " + postId + " doesn't exist.";
      logger.error(errorMessage);
      throw new BadRequestException(errorMessage);
    }
  }

  /**
   * Method to set the image id in a post entity.
   *
   * @param postId  The id of the post to be updated.
   * @param imageId The id of the image to be associated with the post.
   */
  private void updateImageInPost(UUID postId, UUID imageId, String accessUri) {
    Optional<Post> fetchedPost;
    try {
      fetchedPost = postRepository.findById(postId);
      logger.info("Fetched post with id: " + postId);
    } catch (Exception e) {
      throw new EntityNotFoundException(e.getMessage(), e);
    }
    if (fetchedPost.isPresent()) {
      Post post = fetchedPost.get();
      if (StringUtils.isNotBlank(accessUri)) post.setImageAccessUri(accessUri);
      if (StringUtils.isNotBlank(postId.toString())) post.setImageId(imageId);
      getUpdatedPostDTO(post);
    } else {
      String errorMessage = "Post with id: " + postId + " doesn't exist.";
      logger.error(errorMessage);
      throw new BadRequestException(errorMessage);
    }
  }

  /**
   * Method to update a post entity after any changes.
   *
   * @param post The post entity to be updated.
   * @return PostDTO The updated post.
   */
  private PostDTO getUpdatedPostDTO(Post post) {
    try {
      Post updatedPost = postRepository.save(post);
      logger.info("Post updated successfully with id: " + post.getId());
      return getPostToDTO(updatedPost);
    } catch (Exception e) {
      logger.error("Error while updating post with id: " + post.getId(), e);
      throw new EntityFailedUpdateException(e.getMessage(), e);
    }
  }

  /**
   * Method to get the top posts based on the number of comments.
   * Fulfils story - As a user, I should be able to get the list of all posts along with the last 2 comments
   * on each post
   * Fulfils requirement - Retrieve posts via a cursor-based pagination
   * Fetches posts from db with a page size of 10.
   *
   * @param pageable The page request
   * @return Page<PostDTO> The fetched posts
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")
  public Page<PostDTO> getTopPosts(Pageable pageable) {
    try {
      Page<Post> allPosts = postRepository.findAllByOrderByCommentsCountDescCreatedAtDesc(pageable);
      Page<PostDTO> postDTOPage = allPosts.map(this::getPostToDTO);
      for (PostDTO postDTO : postDTOPage) {
        Optional<List<Comment>> postComments =
            commentService.getByPostIdOrderByCreatedAtDesc(postDTO.getId(), Pageable.ofSize(NUMBER_OF_COMMENTS_PER_POST));
        addCommentsDTOToPost(postDTO, postComments);
      }
      return postDTOPage;
    } catch (Exception e) {
      throw new ContentServerException("Error while fetching top posts", e);
    }
  }

  /**
   * Fetches all posts from db with a page size of 10.
   *
   * @param pageable The page request
   * @return Page<PostDTO> The fetched posts
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")
  public Page<PostDTO> getAllPosts(Pageable pageable) {
    try {
      Page<Post> allPosts = postRepository.findAll(pageable);
      return allPosts.map(this::getPostToDTO);
    } catch (Exception e) {
      throw new ContentServerException("Error while fetching all posts", e);
    }
  }

  /**
   * Get all comments for a post
   *
   * @param postId Post id
   * @return List of comments for the post
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")
  public PostDTO getCommentsForAPost(String postId) {
    PostDTO postDTO = getPost(postId);
    Optional<List<Comment>> postComments = commentService.getCommentsByPostId(UUID.fromString(postId));
    addCommentsDTOToPost(postDTO, postComments);
    return postDTO;
  }

  private void addCommentsDTOToPost(PostDTO postDTO, Optional<List<Comment>> postComments) {
    if(postComments.isPresent()) {
      List<CommentDTO> commentDTOList = new ArrayList<>();
      postComments.get().forEach(comment -> {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        commentDTOList.add(commentDTO);
      });
      postDTO.setComments(commentDTOList);
    } else {
      postDTO.setComments(new ArrayList<>());
    }
  }

  private PostDTO getPostToDTO(Post post) {
    return modelMapper.map(post, PostDTO.class);
  }

  /**
   * Increments the comments count for a given post.
   * MongoDB's $inc operator inherently supports atomic updates.
   * So an explicit lock is not required to maintain data integrity. In case the db doesn't support atomic updates,
   * we can use an explicit lock.
   * In a production environment, the @Async functionality can be achieved using even driven architecture,
   * which will take the load of this service.
   *
   * @param postId The id of the post for which the comment is to be created.
   */
  @Async("taskExecutor")
  public void incrementCommentCount(UUID postId) {
    Query query = new Query(Criteria.where("id").is(postId));
    Update update = new Update().inc("commentsCount", 1);
    mongoTemplate.updateFirst(query, update, Post.class);
  }


  /**
   * Decrements the comments count for a given post.
   * MongoDB's $inc operator inherently supports atomic updates.
   * So an explicit lock is not required to maintain data integrity. In case the db doesn't support atomic updates,
   * * we can use an explicit lock.
   * In a production environment, the @Async functionality can be achieved using even driven architecture,
   * which will take the load of this service.
   *
   * @param postId
   */
  @Async("taskExecutor")
  public void decrementCommentCount(UUID postId) {
    Query query = new Query(Criteria.where("_id").is(postId));
    Update update = new Update().inc("commentCount", -1);
    mongoTemplate.updateFirst(query, update, Post.class);
  }

  /**
   * Method to handle rate limit fallback for the service.
   *
   * @param exception The exception thrown
   * @return PostDTO The fallback response
   */
  @Override
  protected PostDTO localRateLimitFallback(RequestNotPermitted exception) {
    circuitBreakerConfig.rateLimitFallback(exception);
    return new PostDTO();
  }

  /**
   * Method to handle circuit breaker fallback for the service.
   *
   * @param exception The exception thrown
   * @return PostDTO The fallback response
   */
  @Override
  protected PostDTO localCircuitBreakerFallback(RequestNotPermitted exception) {
    circuitBreakerConfig.circuitBreakerFallback(exception);
    return new PostDTO();
  }

  private Page<PostDTO> localRateLimitFallback(Pageable pageable, RequestNotPermitted exception) {
    circuitBreakerConfig.rateLimitFallback(exception);
    return Page.empty();
  }

  protected Page<PostDTO> localCircuitBreakerFallback(Pageable pageable, RequestNotPermitted exception) {
    circuitBreakerConfig.circuitBreakerFallback(exception);
    return Page.empty();
  }

}


