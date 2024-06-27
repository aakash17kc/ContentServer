package com.aakash.contentserver.entities;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Post entity. This class can be used to handle posts entity request.

 */
@Document(collection = "posts")
public class Post extends Content {


  private long commentsCount;
  public Post() {
  }
  
  public long getCommentsCount() {
    return commentsCount;
  }
  
  public void setCommentsCount(long commentsCount) {
    this.commentsCount = commentsCount;
  }
}
