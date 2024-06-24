package com.aakash.contentserver.exceptions;

/**
 * ImageProcessingException class. This class is used to handle any processing exceptions

 */
public class ContentServerException extends RuntimeException{
  public ContentServerException(String message){
    super(message);
  }
  public ContentServerException(String message, Throwable th){
    super(message,th);
  }
  public ContentServerException(Throwable th){
    super(th);
  }
}
