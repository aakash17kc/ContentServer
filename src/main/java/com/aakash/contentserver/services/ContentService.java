package com.aakash.contentserver.services;

import com.aakash.contentserver.configuration.CircuitBreakerConfiguration;
import com.aakash.contentserver.entities.Content;
import com.aakash.contentserver.exceptions.BadRequestException;
import com.aakash.contentserver.exceptions.ContentServerException;
import com.aakash.contentserver.repositories.CommentsRepository;
import com.aakash.contentserver.repositories.PostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract class for common operations related to Post and Comment entities.
 * @param <T>
 */
@Component
public abstract class ContentService<T> extends BackendService {

  protected final CommentsRepository commentsRepository;
  protected final PostRepository postRepository;
  protected final Validator validator;

  @Autowired
  public ContentService(CircuitBreakerConfiguration circuitBreakerConfig, ModelMapper modelMapper, MongoTemplate mongoTemplate,
                        ObjectMapper objectMapper, Clock clock, CommentsRepository commentsRepository, PostRepository postRepository, Validator validator) {
    super(circuitBreakerConfig, modelMapper, mongoTemplate, objectMapper, clock);
    this.commentsRepository = commentsRepository;
    this.postRepository = postRepository;
    this.validator = validator;
  }

  public <K extends Content> void validateEntity(K content ){
    Set<ConstraintViolation<K>> violations = validator.validate(content);
    if (!violations.isEmpty()) {
      try {
        Map<String, String> violationsMap = violations.stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));
        throw new BadRequestException(objectMapper.writeValueAsString(violationsMap));
      } catch (JsonProcessingException e) {
        throw new ContentServerException("Error converting entity violations to string ", e);
      }
    }
  }

  protected abstract T localRateLimitFallback(RequestNotPermitted exception);

  protected abstract T localCircuitBreakerFallback( RequestNotPermitted exception);
}
