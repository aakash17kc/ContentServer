package com.aakash.contentserver.configuration;

import org.springframework.hateoas.RepresentationModel;

public class ApiError extends RepresentationModel<ApiError> {
  private String message;
  private String details;

  public ApiError(String message, String details) {
    this.message = message;
    this.details = details;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}

