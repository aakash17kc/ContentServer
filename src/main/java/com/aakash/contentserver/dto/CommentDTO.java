package com.aakash.contentserver.dto;

/**
 * CommentDTO class. This class can be used to handle responses to comment entity request.
 */
public class CommentDTO extends ContentDTO {
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
