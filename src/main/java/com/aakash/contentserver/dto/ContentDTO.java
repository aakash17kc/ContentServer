package com.aakash.contentserver.dto;

import java.time.Instant;
import java.util.UUID;

public class ContentDTO {
  private UUID id;
  private String content;
  private String creator;
  private Instant createdAt;

  private UUID imageId;

  private String imageAccessUri;

  public ContentDTO() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public UUID getImageId() {
    return imageId;
  }

  public void setImageId(UUID imageId) {
    this.imageId = imageId;
  }

  public String getImageAccessUri() {
    return imageAccessUri;
  }

  public void setImageAccessUri(String imageAccessUri) {
    this.imageAccessUri = imageAccessUri;
  }
}
