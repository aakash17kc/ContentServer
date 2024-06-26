package com.aakash.contentserver.entities;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

/**
 * Content entity. This class acts as a super class for content
 */
public class Content {

  @Id
  @Indexed
  private UUID id;

  /**
   * Content of the activity. It can be a post or a comment.
   * The content of a post can be empty but the content of a comment cannot be empty.
   * In case it's not empty for both cases, we can add @NotNull(message = " ")
   * to validate the if the field is present in the request body.
   */
  @NotBlank(message = "content/caption cannot be empty")
  private String content;

  @NotBlank(message = "creator cannot be empty")
  private String creator;
  @CreatedDate
  @Indexed
  private Instant createdAt;

  private UUID imageId;

  private String imageAccessUri;

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
