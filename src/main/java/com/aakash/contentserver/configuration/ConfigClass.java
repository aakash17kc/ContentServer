package com.aakash.contentserver.configuration;

import com.aakash.contentserver.dto.PostDTO;
import com.aakash.contentserver.entities.Post;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.CacheControl;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class to create beans for the application.
 */
@Configuration
public class ConfigClass {
  /**
   * Method to create a ModelMapper bean.
   * The ModelMapper is used to map between different entities and DTOs.
   *
   * @return ModelMapper
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    //Sets Long type to zero incase the source is null.
    Converter<Long, Long> toNonNullLong = context -> context.getSource() == null ? 0L : context.getSource();
    // Sets the list field to an empty list if the source is null.
    modelMapper.addMappings(new PropertyMap<Post, PostDTO>() {
      @Override
      protected void configure() {
        map().setComments(new ArrayList<>());
      }
    });
    modelMapper.createTypeMap(Long.class, Long.class).setConverter(toNonNullLong);
    
    return modelMapper;
  }
  
  /**
   * Jakarta Validation bean to validate the request body against the entity constraints.
   *
   * @return Validator
   */
  @Bean
  public Validator getValidator() {
    return Validation.buildDefaultValidatorFactory().getValidator();
  }
  
  /**
   * Method to create an ObjectMapper bean.
   * The ObjectMapper is used to serialize and deserialize the objects.
   * The ObjectMapper is configured to indent the output and serialize the dates as ISO-8601.
   * The ObjectMapper is also configured to serialize the dates as ISO-8601.
   *
   * @return ObjectMapper
   */
  @Bean
  public ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return objectMapper;
  }
  
  /**
   * Method to create a Clock bean.
   * The Clock is used to get the current time in the UTC timezone.
   *
   * @return Clock bean
   */
  @Bean
  public Clock getClock() {
    return Clock.systemUTC();
  }
  
  /**
   * Method to create a S3Client bean.
   * The S3Client is used to interact with the AWS S3 service.
   *
   * @return S3Client bean
   */
  @Bean
  public S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.AP_SOUTH_2)
        .build();
  }
  
  @Bean
  AuditorAware<String> auditorProvider() {
    return () -> Optional.of("Administrator");
  }
  
  @Bean
  CacheControl getCacheControl() {
    return CacheControl.maxAge(5, TimeUnit.SECONDS).cachePublic();
  }
}
