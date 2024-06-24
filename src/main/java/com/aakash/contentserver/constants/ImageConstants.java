package com.aakash.contentserver.constants;

/**
 * The ImageConstants class is used to store the constants used for image processing.
 */
public class ImageConstants {
  /**
   * The HEIGHT constant. Used to fetch the height from the configuration file.
   */
  public static final String HEIGHT = "height";
  /**
   * The WIDTH constant. Used to fetch the width from the configuration file.
   */
  public static final String WIDTH = "width";
  /**
   * The QUALITY constant. Used to fetch the quality from the configuration file.
   */
  public static final String QUALITY = "quality";
  /**
   * The TYPE constant.
   */
  public static final String TYPE = "type";
  /**
   * The configuration file that contains the image resize configuration as per activity.
   * The configuration file is stored in the resources folder in JSON format.
   *
   */
  public static final String IMAGE_RESIZE_CONFIGURATION_FILE = "resize_config.json";
  /**
   * The ORIGINAL_LOCATION constant. Used to store the original image location.
   */
  public static final String ORIGINAL_LOCATION = "original";
  /**
   * The COMPRESSED_LOCATION constant. Used to store the compressed image location.
   */
  public static final String COMPRESSED_LOCATION = "compressed";
  /**
   * The expiration for the signed S3 URL in days.
   */
  public static final long SIGNED_URL_EXPIRATION_DAYS = 30;
  /**
   * The part size for the Image multipart upload.
   */
  public static final int PART_SIZE = 5 * 1024 * 1024;
  /**
   * The access URI for the image.
   */
  public static final String ACCESS_URI = "http://localhost:8080/v1/images/";
  /**
   * The content endpoint.
   */
  public static final String CONTENT_ENDPOINT = "/content";
}
