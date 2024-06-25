package com.aakash.contentserver.services;

import com.aakash.contentserver.configuration.CircuitBreakerConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;

/**
 * Abstract class to provide common functionality for all backend APIs.
 */
@Component
public abstract class BackendService {
  protected final CircuitBreakerConfiguration circuitBreakerConfig;
  protected final ModelMapper modelMapper;
  protected final MongoTemplate mongoTemplate;
  protected final ObjectMapper objectMapper;
  protected final Clock clock;

  @Autowired
  protected BackendService(CircuitBreakerConfiguration circuitBreakerConfig, ModelMapper modelMapper, MongoTemplate mongoTemplate, ObjectMapper objectMapper, Clock clock) {
    this.circuitBreakerConfig = circuitBreakerConfig;
    this.modelMapper = modelMapper;
    this.mongoTemplate = mongoTemplate;
    this.objectMapper = objectMapper;
    this.clock = clock;
  }

}
