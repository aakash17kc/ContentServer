package com.aakash.contentserver.services;

import com.aakash.contentserver.configuration.CircuitBreakerConfiguration;
import com.aakash.contentserver.entities.Post;
import com.aakash.contentserver.impl.ImageSupportedTypeImpl;
import com.aakash.contentserver.processors.ImageProcessor;
import com.aakash.contentserver.repositories.CommentsRepository;
import com.aakash.contentserver.repositories.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {


  @Mock
  private PostRepository postRepository;

  @Mock
  private ModelMapper modelMapper;

  @Mock
  private MongoTemplate mongoTemplate;

  @Mock
  private ObjectMapper objectMapper;

  @Mock
  private Clock clock;

  @Mock
  private ImageProcessor imageProcessor;

  @Mock
  private ImageService imageService;

  @Mock
  private CircuitBreakerConfiguration circuitBreakerConfig;
  @Mock
  Validator validator;

  @Mock
  CommentsRepository commentsRepository;

  @InjectMocks
  private PostService postService;
  @Mock
  private ImageSupportedTypeImpl imageSupportedTypeImpl;
  @Mock
  CommentService commentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    postService = new PostService(postRepository, modelMapper, mongoTemplate, objectMapper, clock, commentsRepository, validator, imageProcessor, imageService, circuitBreakerConfig,   commentService);
  }

  @Test
  void savePost() throws Exception {

  }

  @Test
  void getPost() {
    UUID postId = UUID.randomUUID();
    Post post = new Post();
    post.setId(postId);

    when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));

    postService.getPost(postId.toString());

    verify(postRepository, times(1)).findById(any(UUID.class));
  }

  @Test
  void updatePost() {
      UUID postId = UUID.randomUUID();
      UUID imageId = UUID.randomUUID();
      String caption = "Updated Caption";
      String creator = "Updated Creator";
      MultipartFile file = new MockMultipartFile("file", "Hello, World!".getBytes());

      Post post = new Post();
      post.setId(postId);
      post.setContent(caption);
      post.setCreator(creator);
      post.setImageId(imageId);

      when(postRepository.findById(any(UUID.class))).thenReturn(Optional.of(post));
      when(postRepository.save(any(Post.class))).thenReturn(post);
      
  }

  //TODO: Add more tests for PostService
  @Test
  void getTopPosts() {
  }

  @Test
  void getAllPosts() {
  }
}