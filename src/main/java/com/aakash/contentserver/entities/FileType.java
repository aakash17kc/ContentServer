package com.aakash.contentserver.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

/**
 * FileType entity. This class acts as a super class for file type entities.
 * Currently, only Image is the child class of FileType. We can add more types when needed.
 */
public class FileType {
  @Id
  @Indexed
  private UUID id;
  private String bucketName;

  private String location;

  private String accessUri;

  private Long sizeInKB;

  private String type;

  private Instant createdAt;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getBucketName() {
    return bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getAccessUri() {
    return accessUri;
  }

  public void setAccessUri(String accessUri) {
    this.accessUri = accessUri;
  }

  public Long getSizeInKB() {
    return sizeInKB;
  }

  public void setSizeInKB(Long sizeInKB) {
    this.sizeInKB = sizeInKB;
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
