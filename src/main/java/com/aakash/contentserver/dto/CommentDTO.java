package com.aakash.contentserver.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

import java.time.Instant;

/**
 * CommentDTO class. This class can be used to handle responses to comment entity request.
 */
public class CommentDTO extends ActivityDTO{
  private String postId;
  public CommentDTO() {
  }

  public String getPostId() {
    return postId;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }
}
