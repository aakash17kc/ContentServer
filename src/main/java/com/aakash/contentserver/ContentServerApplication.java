package com.aakash.contentserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
public class ContentServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(ContentServerApplication.class, args);
  }

}
