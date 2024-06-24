package com.aakash.contentserver.exceptions;

/**
 * ImageProcessingException class. This class is used to handle if an entity is not found.

 */
public class EntityNotFoundException extends UserException{
  public EntityNotFoundException(String message) {
    super(message);
  }

  public EntityNotFoundException(String message, Throwable th) {
    super(message, th);
  }

  public EntityNotFoundException(Throwable th) {
    super(th);
  }
}
