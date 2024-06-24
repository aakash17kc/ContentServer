package com.aakash.contentserver.interfaces;

import com.aakash.contentserver.enums.ActivityType;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Interface for ImageResizeConfigurationImpl.
 */
public interface ImageResizeConfiguration {
  /**
   * Fetches configuration from {resize_configuration.json}.
   *
   * @param type Configuration type.
   * @return JsonObject Configuration object.
   */
  JsonNode getImageConfigurationByActivity(ActivityType type);
}
