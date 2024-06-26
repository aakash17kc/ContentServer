package com.aakash.contentserver.entities;

import com.aakash.contentserver.enums.ImageType;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Image entity. This class can be used to handle associate an image with a post.
 */
@Document(collection = "images")
public class Image extends FileType {
  private UUID postId;

  private UUID commentId;

  public Image() {
  }

  public UUID getPostId() {
    return postId;
  }

  public void setPostId(UUID postId) {
    this.postId = postId;
  }

  public UUID getCommentId() {
    return commentId;
  }

  public void setCommentId(UUID commentId) {
    this.commentId = commentId;
  }
}
