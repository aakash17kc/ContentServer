package com.aakash.contentserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
@EnableMongoAuditing
public class ContentServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ContentServerApplication.class, args);
  }

}
