package com.aakash.contentserver.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Post entity. This class can be used to handle posts entity request.

 */
@Document(collection = "posts")
public class Post extends Content {

  private UUID imageId;

  private String imageAccessUri;
  private Long commentsCount;
  public Post() {
  }
  public UUID getImageId() {
    return imageId;
  }

  public void setImageId(UUID imageId) {
    this.imageId = imageId;
  }

  public Long getCommentsCount() {
    return commentsCount;
  }

  public void setCommentsCount(Long commentsCount) {
    this.commentsCount = commentsCount;
  }

  public String getImageAccessUri() {
    return imageAccessUri;
  }

  public void setImageAccessUri(String imageAccessUri) {
    this.imageAccessUri = imageAccessUri;
  }
}
