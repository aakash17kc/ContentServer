package com.aakash.contentserver.configuration;

import com.aakash.contentserver.exceptions.ContentServerException;
import com.aakash.contentserver.exceptions.RateLimitException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
/**
 * Configuration class to handle circuit breaker and rate limiter fallbacks.
 */
@Component
public class CircuitBreakerConfiguration {
  Logger logger = LoggerFactory.getLogger(CircuitBreakerConfiguration.class);
  public void rateLimitFallback(RequestNotPermitted exception) {
    logger.error("Rate limit exceeded. Calling fallback method.");
    throw new RateLimitException("Rate limit exceeded", exception);
  }
  public void circuitBreakerFallback(Exception exception) {
    logger.error("Circuit breaker is open. Calling fallback method.");
    throw new ContentServerException("Circuit breaker is open. Something's not right", exception);
  }
}
