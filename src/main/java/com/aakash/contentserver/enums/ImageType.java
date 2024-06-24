package com.aakash.contentserver.enums;

/**
 *  ImageType enum. This enum has the validate image types.
 */
public enum ImageType {
  JPG("jpg"),
  PNG("png"),
  BMP("bmp");

  private final String value;

  ImageType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
