package com.aakash.contentserver.enums;

/**
 * ActivityType enum. This enum has the validate activity types.
 * It's used to fetch the image configuration from resize_config.json
 */
public enum ActivityType {
  POST("post"),
  COMMENT("comment");

  private final String type;
  ActivityType(String type) {
    this.type = type;
  }
  public String getValue() {
    return type;
  }
}
