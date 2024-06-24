package com.aakash.contentserver.impl;

import com.aakash.contentserver.constants.ImageConstants;
import com.aakash.contentserver.enums.ActivityType;
import com.aakash.contentserver.exceptions.ImageProcessingException;
import com.aakash.contentserver.interfaces.ImageResizeConfiguration;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class ImageResizeConfigurationImpl implements ImageResizeConfiguration {

private final JsonNode jsonNode;

  private final ObjectMapper objectMapper;

  public ImageResizeConfigurationImpl(ObjectMapper objectMapper) throws ImageProcessingException {

    this.objectMapper = objectMapper;
    try {
      InputStream  resource = getClass().getClassLoader().getResourceAsStream(ImageConstants.IMAGE_RESIZE_CONFIGURATION_FILE);
      this.jsonNode = objectMapper.readValue(resource, JsonNode.class);
    } catch (Exception e) {
      throw new ImageProcessingException("Error while reading image configuration file. ", e);
    }
  }

  @Override
  public JsonNode getImageConfigurationByActivity(ActivityType activityType) {
    return jsonNode.get(activityType.getValue());
  }
}
