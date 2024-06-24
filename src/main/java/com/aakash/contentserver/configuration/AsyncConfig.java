package com.aakash.contentserver.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class to enable async processing.
 */
@Configuration
@EnableAsync
public class AsyncConfig {
  /**
   * Method to create a thread pool executor.
   * The @Async("taskExecutor") annotation is used to specify the executor to be used for the async method.
   * @return Executor
   */
  @Bean(name = "taskExecutor")
  public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(10);
    executor.setMaxPoolSize(100);
    executor.setQueueCapacity(150);
    executor.initialize();
    return executor;
  }
}
