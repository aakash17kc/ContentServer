package com.aakash.contentserver.exceptions;

/**
 * ImageProcessingException class. This class is used to handle when endity update fails.

 */
public class EntityFailedUpdateException extends RuntimeException{
  public EntityFailedUpdateException(String message){
    super(message);
  }
  public EntityFailedUpdateException(String message, Throwable th){
    super(message,th);
  }
  public EntityFailedUpdateException(Throwable th){
    super(th);
  }
}
