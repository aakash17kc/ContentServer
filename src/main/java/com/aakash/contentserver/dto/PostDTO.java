package com.aakash.contentserver.dto;

import com.aakash.contentserver.entities.Comment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PostDTO class. This class can be used to return responses for Post entity requests.

 */
public class PostDTO extends ActivityDTO{

  public String imageId;
  public int commentsCount;
  public List<CommentDTO> comments;

  private String imageAccessUri;
  public PostDTO() {
  }

  public String getImageId() {
    return imageId;
  }

  public void setImageId(String imageId) {
    this.imageId = imageId;
  }

  public int getCommentsCount() {
    return commentsCount;
  }

  public void setCommentsCount(int commentsCount) {
    this.commentsCount = commentsCount;
  }

  public List<CommentDTO> getComments() {
    return comments;
  }

  public void setComments(List<CommentDTO> comments) {
    this.comments = comments;
  }

  public String getImageAccessUri() {
    return imageAccessUri;
  }

  public void setImageAccessUri(String imageAccessUri) {
    this.imageAccessUri = imageAccessUri;
  }
}
