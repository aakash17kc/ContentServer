package com.aakash.contentserver.configuration;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ShutdownHook {
  Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
  @PostConstruct
  public void init() {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      logger.info("Application is shutting down");
    }));
  }
}