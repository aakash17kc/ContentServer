package com.aakash.contentserver.dto;

/**
 * ImageDTO class. This class is used to handle image data transfer objects.
 */
public class ImageDTO extends FileDTO{
  private String postId;

  public ImageDTO() {
  }

  public String getPostId() {
    return postId;
  }

  public void setPostId(String postId) {
    this.postId = postId;
  }
}
