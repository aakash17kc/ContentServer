package com.aakash.contentserver.exceptions;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;

/**
 * RateLimitException class. This class is used to handle rate limit exceptions.
 */
public class RateLimitException extends RuntimeException {

  public RateLimitException(String rateLimitExceeded, RequestNotPermitted requestNotPermittedException) {
    super(requestNotPermittedException.getMessage());
  }
}
