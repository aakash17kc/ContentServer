package com.aakash.contentserver.services;

import com.aakash.contentserver.configuration.CircuitBreakerConfiguration;
import com.aakash.contentserver.dto.CommentDTO;
import com.aakash.contentserver.dto.PostDTO;
import com.aakash.contentserver.entities.Comment;
import com.aakash.contentserver.exceptions.BadRequestException;
import com.aakash.contentserver.exceptions.ContentServerException;
import com.aakash.contentserver.exceptions.EntityNotFoundException;
import com.aakash.contentserver.repositories.CommentsRepository;
import com.aakash.contentserver.repositories.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.*;

import static com.aakash.contentserver.constants.CommonConstants.NUMBER_OF_COMMENTS_PER_POST;

/**
 * Service class to handle CRUD operations for Comment entity.
 */
@Service
public class CommentService extends ContentService<CommentDTO> {

  private final Logger logger;
  private final PostService postService;

  @Autowired
  public CommentService(CircuitBreakerConfiguration circuitBreakerConfig, ModelMapper modelMapper,
                        MongoTemplate mongoTemplate, ObjectMapper objectMapper, Clock clock, CommentsRepository commentsRepository,
                        PostRepository postRepository, Validator validator, @Lazy PostService postService) {

    super(circuitBreakerConfig, modelMapper, mongoTemplate, objectMapper, clock, commentsRepository, postRepository, validator);
    this.logger = LoggerFactory.getLogger(CommentService.class);
    this.postService = postService;
  }

  /**
   * Method to save a comment for a post
   *
   * @param postId  Post id
   * @param comment Comment request payload
   * @return CommentDTO
   * @throws EntityNotFoundException If the post doesn't exist
   * @throws ContentServerException  If there is an issue processing the request
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")
  public CommentDTO saveCommentForPost(String postId, Comment comment) throws EntityNotFoundException, ContentServerException {

    PostDTO postDTO = postService.getPost(postId);
    if (postDTO == null) {
      throw new EntityNotFoundException("Unable to add comment to a non existing post.");
    }
    validateEntity(comment);
    comment.setId(UUID.randomUUID());
    comment.setPostId(UUID.fromString(postId));
    comment.setCreatedAt(Instant.now(clock));
    try {
      Comment savedComment = commentsRepository.save(comment);
      postService.incrementCommentCount(savedComment.getPostId());
      return modelMapper.map(savedComment, CommentDTO.class);
    } catch (Exception e) {
      throw new ContentServerException("Error while saving comment", e);
    }
  }

  /**
   * Method to get a comment from DB
   *
   * @param commentId Comment id
   * @return CommentDTO
   * @throws EntityNotFoundException If the comment doesn't exist
   * @throws ContentServerException  If there is an issue processing the request
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")

  public CommentDTO getComment(String commentId) throws EntityNotFoundException, ContentServerException {
    Optional<Comment> comment;
    try {
      comment = commentsRepository.findById(UUID.fromString(commentId));
    } catch (Exception e) {
      throw new ContentServerException("Error while fetching comment", e);
    }
    if (comment.isPresent()) {
      return modelMapper.map(comment, CommentDTO.class);
    } else {
      String errorMessage = "Comment with id " + commentId + " doesn't exist.";
      logger.error(errorMessage);
      throw new EntityNotFoundException(errorMessage);
    }
  }

  /**
   * Method to delete a comment
   * Only delete the comment if the comment creator is same as the one requesting to delete.
   *
   * @param commentId Comment id
   * @param creator   Comment creator
   * @throws BadRequestException     If the comment doesn't belong to the creator
   * @throws EntityNotFoundException If the comment doesn't exist
   */
  @RateLimiter(name = "rateLimiterAppWide", fallbackMethod = "localRateLimitFallback")
  @CircuitBreaker(name = "circuitBreakerAppWide", fallbackMethod = "localCircuitBreakerFallback")
  public String deleteComment(String commentId, String creator) throws BadRequestException, EntityNotFoundException {
    Optional<Comment> comment;
    try {
      comment = commentsRepository.findById(UUID.fromString(commentId));
    } catch (Exception e) {
      String errorMessage = "Error while fetching comment with id " + commentId;
      logger.error(errorMessage);
      throw new ContentServerException(errorMessage, e);
    }
    if (comment.isPresent()) {
      if (!comment.get().getCreator().equals(creator)) {
        String errorMessage = "Comment with id " + commentId + " doesn't belong to the creator.";
        logger.error(errorMessage);
        throw new BadRequestException(errorMessage);
      }
      commentsRepository.deleteById(UUID.fromString(commentId));
      logger.info("Comment with id {} deleted successfully", commentId);
      postService.decrementCommentCount(comment.get().getPostId());
      return "Comment deleted successfully";
    } else {
      String errorMessage = "Comment with id " + commentId + " doesn't exist.";
      logger.error(errorMessage);
      throw new EntityNotFoundException(errorMessage);
    }
  }

  /**
   * Fallback method for rate limiter
   *
   * @param exception Exception
   * @return CommentDTO
   */
  @Override
  protected CommentDTO localRateLimitFallback(RequestNotPermitted exception) {
    circuitBreakerConfig.rateLimitFallback(exception);
    return new CommentDTO();
  }

  /**
   * Fallback method for circuit breaker
   *
   * @param exception Exception
   * @return CommentDTO
   */
  @Override
  protected CommentDTO localCircuitBreakerFallback(RequestNotPermitted exception) {
    circuitBreakerConfig.circuitBreakerFallback(exception);
    return new CommentDTO();
  }

  /**
   * Get comments for a post in descending order of creation time
   * @param id Post id
   * @param pageable Pageable
   * @return List of comments
   */
  public Optional<List<Comment>> getByPostIdOrderByCreatedAtDesc(UUID id, Pageable pageable) {
    return commentsRepository.findByPostIdOrderByCreatedAtDesc(id, Pageable.ofSize(NUMBER_OF_COMMENTS_PER_POST));
  }

  /**
   * Get all comments for a post
   * @param uuid Post id
   * @return List of comments
   */
  public Optional<List<Comment>> getCommentsByPostId(UUID uuid) {
    return commentsRepository.findByPostId(uuid);
  }
}