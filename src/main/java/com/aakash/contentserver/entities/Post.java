package com.aakash.contentserver.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Post entity. This class can be used to handle posts entity request.

 */
@Document(collection = "posts")
public class Post extends Content {


  private Long commentsCount;
  public Post() {
  }

  public Long getCommentsCount() {
    return commentsCount;
  }

  public void setCommentsCount(Long commentsCount) {
    this.commentsCount = commentsCount;
  }

}
