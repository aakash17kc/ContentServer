package com.aakash.contentserver.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

public abstract class FileDTO {
  private String id;

  private String accessUri;

  private String type;

  private Instant createdAt;

  public FileDTO() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAccessUri() {
    return accessUri;
  }

  public void setAccessUri(String accessUri) {
    this.accessUri = accessUri;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
