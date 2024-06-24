package com.aakash.contentserver.services;

import com.aakash.contentserver.configuration.CircuitBreakerConfiguration;
import com.aakash.contentserver.dto.CommentDTO;
import com.aakash.contentserver.entities.Comment;
import com.aakash.contentserver.entities.Post;
import com.aakash.contentserver.repositories.CommentsRepository;
import com.aakash.contentserver.repositories.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CommentServiceTest {

  @Mock
  private CommentsRepository commentsRepository;

  @Mock
  private PostRepository postRepository;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private PostService postService;
  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private ObjectMapper objectMapper;
  @Mock
  Validator validator;

  @Mock
  private Clock clock;

  private CommentService commentService;
  @Mock
  private CircuitBreakerConfiguration circuitBreakerConfig;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    commentService = new CommentService(circuitBreakerConfig, modelMapper, mongoTemplate, objectMapper, clock, commentsRepository, postRepository, validator,postService);
  }

  @Test
  void saveCommentForPost() throws Exception {
    UUID postId = UUID.randomUUID();
    UUID commentId = UUID.randomUUID();
    Comment comment = new Comment();
    comment.setId(commentId);
    comment.setPostId(postId);
    comment.setCreatedAt(Instant.now(clock));

    Post post = new Post();
    post.setId(postId);

    CommentDTO commentDTO = new CommentDTO();
    commentDTO.setId(commentId);

    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));
    when(commentsRepository.save(any(Comment.class))).thenReturn(comment);
    when(modelMapper.map(any(Comment.class), eq(CommentDTO.class))).thenReturn(commentDTO);

    CommentDTO savedComment = commentService.saveCommentForPost(postId.toString(), comment);

    verify(postRepository, times(1)).findById(any(UUID.class));
    verify(commentsRepository, times(1)).save(any(Comment.class));
    verify(postService, times(1)).incrementCommentCount(any(UUID.class));
  }

  //TODO: Add more tests for the CommentService
  @Test
  void getComment() {
  }

  @Test
  void deleteComment() {
  }

  @Test
  void getCommentsForPost() {
  }
}