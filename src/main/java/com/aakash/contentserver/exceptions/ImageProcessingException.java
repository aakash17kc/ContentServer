package com.aakash.contentserver.exceptions;

/**
 * ImageProcessingException class. This class is used to handle image processing exceptions.

 */
public class ImageProcessingException extends RuntimeException {

  public ImageProcessingException(String message) {
    super(message);
  }

  public ImageProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
