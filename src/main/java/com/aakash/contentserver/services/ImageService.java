package com.aakash.contentserver.services;

import com.aakash.contentserver.configuration.CircuitBreakerConfiguration;
import com.aakash.contentserver.dto.ImageDTO;
import com.aakash.contentserver.entities.Image;
import com.aakash.contentserver.exceptions.EntityNotFoundException;
import com.aakash.contentserver.processors.ImageProcessor;
import com.aakash.contentserver.repositories.ImageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class for Image entity related operations.
 */
@Service
public class ImageService extends BackendService {
  private final ImageRepository imageRepository;
  private final ImageProcessor imageProcessor;
  
  @Autowired
  public ImageService(CircuitBreakerConfiguration circuitBreakerConfig, ModelMapper modelMapper, Clock clock,
                      MongoTemplate mongoTemplate, ImageRepository imageRepository, ObjectMapper objectMapper, ImageProcessor imageProcessor) {
    super(circuitBreakerConfig, modelMapper, mongoTemplate, objectMapper, clock);
    this.imageRepository = imageRepository;
    this.imageProcessor = imageProcessor;
  }
  
  /**
   * Method to save the image entity to DB
   *
   * @param image The image entity
   * @return The saved image entity
   */
  public ImageDTO saveImage(Image image) {
    Image savedImage = imageRepository.save(image);
    return modelMapper.map(savedImage, ImageDTO.class);
  }
  
  /**
   * Method to get the image entity from DB
   *
   * @param imageId The image id
   * @return The image entity
   */
  
  public ImageDTO getImage(String imageId) {
    Optional<Image> image = imageRepository.findById(UUID.fromString(imageId));
    if (image.isEmpty()) {
      throw new EntityNotFoundException("Image not found with id: " + imageId);
    }
    return modelMapper.map(image, ImageDTO.class);
  }
  
  /**
   * Method to get the image content from S3 as byte stream.
   *
   * @param imageId The image id
   * @return The image content as byte stream
   * @throws IOException If there is an error in downloading the image from S3
   */
  public byte[] getImageContent(String imageId) throws IOException {
    Optional<Image> image = imageRepository.findById(UUID.fromString(imageId));
    if (image.isEmpty()) {
      throw new EntityNotFoundException("Image not found with id: " + imageId);
    }
    return imageProcessor.downloadImageFromS3(image.get());
  }
  
}
