package com.aakash.contentserver.exceptions;

public class EntityNotValidException extends UserException{
  public EntityNotValidException(String message) {
    super(message);
  }

  public EntityNotValidException(String message, Throwable th) {
    super(message, th);
  }

  public EntityNotValidException(Throwable th) {
    super(th);
  }
}