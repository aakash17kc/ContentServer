package com.aakash.contentserver.services;

import com.aakash.contentserver.configuration.CircuitBreakerConfiguration;
import com.aakash.contentserver.dto.ImageDTO;
import com.aakash.contentserver.entities.Image;
import com.aakash.contentserver.processors.ImageProcessor;
import com.aakash.contentserver.repositories.ImageRepository;
import com.aakash.contentserver.repositories.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Clock;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ImageServiceTest {


  @Mock
  private ImageRepository imageRepository;
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
  private CircuitBreakerConfiguration circuitBreakerConfig;

  @InjectMocks
  private ImageService imageService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  void saveImage() {
    UUID imageId = UUID.randomUUID();
    Image image = Mockito.mock(Image.class);
    ImageDTO imageDTO = new ImageDTO();
    imageDTO.setId(imageId.toString());
    when(imageRepository.save(any(Image.class))).thenReturn(image);
    when(image.getId()).thenReturn(imageId);
    when(modelMapper.map(any(Image.class), eq(ImageDTO.class))).thenReturn(imageDTO);
    ImageDTO savedImage = imageService.saveImage(image);


    verify(imageRepository, times(1)).save(any(Image.class));
    assertEquals(imageId.toString(), savedImage.getId());
  }

  //TODO: Add more tests for ImageService
  @Test
  void getImage() {
  }
}