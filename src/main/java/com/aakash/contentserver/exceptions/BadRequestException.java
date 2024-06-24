package com.aakash.contentserver.exceptions;

/**
 * BadRequestException class. This class is used to handle bad request exceptions during API calls.
 */
public class BadRequestException extends UserException {
  public BadRequestException(String message) {
    super(message);
  }

  public BadRequestException(String message, Throwable th) {
    super(message, th);
  }

  public BadRequestException(Throwable th) {
    super(th);
  }
}
