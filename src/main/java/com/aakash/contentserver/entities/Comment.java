package com.aakash.contentserver.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Comment entity. This class can be used to handle comments entity request.

 */
@Document(collection = "comments")
public class Comment extends Content {

  private UUID postId;

  public Comment() {
  }

  public UUID getPostId() {
    return postId;
  }

  public void setPostId(UUID postId) {
    this.postId = postId;
  }

}
