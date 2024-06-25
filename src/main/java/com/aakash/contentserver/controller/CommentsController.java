package com.aakash.contentserver.controller;

import com.aakash.contentserver.dto.CommentDTO;
import com.aakash.contentserver.entities.Comment;
import com.aakash.contentserver.exceptions.ContentServerException;
import com.aakash.contentserver.exceptions.EntityNotFoundException;
import com.aakash.contentserver.services.CommentService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * Controller class for handling comments requests.
 *
 * @version 1.0
 * @since 24/06/2024
 */
@RestController()
@RequestMapping("/v1/comments")
public class CommentsController {
  private final CommentService commentService;
  private final CacheControl cacheControl;

  public CommentsController(CommentService commentService, CacheControl cacheControl) {
    this.commentService = commentService;
    this.cacheControl = cacheControl;
  }

  /**
   * Create a comment for an existing post.
   *
   * @param postId  The id of the post for which the comment is to be created.
   * @param comment The comment object to be created.
   * @return The created comment object.
   * @throws EntityNotFoundException If the post doesn't exist.
   * @throws ContentServerException  If there is an issue processing the request.
   */
  @PostMapping
  public ResponseEntity<CommentDTO> createComment(@RequestParam String postId, @RequestBody Comment comment)
      throws EntityNotFoundException, ContentServerException {

    CommentDTO savedComment = commentService.saveCommentForPost(postId, comment);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(savedComment)
        .toUri();
    return ResponseEntity
        .created(location)
        .cacheControl(cacheControl)
        .body(savedComment);
  }

  /**
   * Get a comment by its id.
   * @param commentId The id of the comment to be fetched.
   * @return The fetched comment.
   * @throws EntityNotFoundException If the comment doesn't exist.
   * @throws ContentServerException If there is an issue processing the request.
   */
  @GetMapping("/{commentId}")
  public ResponseEntity<CommentDTO> getComment(@PathVariable String commentId)
      throws EntityNotFoundException, ContentServerException {

    CommentDTO fetchedComment = commentService.getComment(commentId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .cacheControl(cacheControl)
        .body(fetchedComment);
  }

  /**
   * Delete a comment by its id if the creator is the same as the comment creator.
   * @param commentId The id of the comment to be deleted.
   * @param creator The creator param.
   * @return The message of the deletion.
   * @throws EntityNotFoundException If the comment doesn't exist.
   * @throws ContentServerException If there is an issue processing the request.
   */
  @DeleteMapping("/{commentId}")
  public ResponseEntity<String> deleteComment(@PathVariable("commentId") String commentId, @RequestParam("creator") String creator)
      throws EntityNotFoundException, ContentServerException {

    String message = commentService.deleteComment(commentId, creator);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .cacheControl(cacheControl)
        .body(message);
  }
}
