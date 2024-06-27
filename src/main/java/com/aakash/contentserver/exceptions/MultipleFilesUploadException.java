package com.aakash.contentserver.exceptions;

public class MultipleFilesUploadException extends RuntimeException {
  public MultipleFilesUploadException(String message) {
    super(message);
  }
}
