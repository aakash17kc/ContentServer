package com.aakash.contentserver.dto;

import java.util.List;

/**
 * PostDTO class. This class can be used to return responses for Post entity requests.

 */
public class PostDTO extends ContentDTO {

  public long commentsCount;
  public List<CommentDTO> comments;

  private String imageAccessUri;
  public PostDTO() {
  }
  
  
  public long getCommentsCount() {
    return commentsCount;
  }
  
  public void setCommentsCount(long commentsCount) {
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
