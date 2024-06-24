package com.aakash.contentserver.exceptions;

public class UserException extends RuntimeException{
  public UserException(String message) {
    super(message);
  }

  public UserException(String message, Throwable th) {
    super(message, th);
  }

  public UserException(Throwable th) {
    super(th);
  }
}
