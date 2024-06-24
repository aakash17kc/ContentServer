package com.aakash.contentserver.repositories;

import com.aakash.contentserver.entities.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentsRepository extends ContentRepository<Comment> {
  /**
   * Find all comments by post id in descending order of created at.
   * @param id UUID
   * @return Optional of list of comments
   */
  public Optional<List<Comment>> findByPostIdOrderByCreatedAtDesc(UUID id);
  /**
   * Find all comments by post id in descending order of created at.
   * @param id UUID
   * @param pageable Pageable
   * @return Optional of list of comments
   */
  public Optional<List<Comment>> findByPostIdOrderByCreatedAtDesc(UUID id, Pageable pageable);
  /**
   * Find all comments by post id.
   * @param id UUID
   * @return Optional of list of comments
   */
  public Optional<List<Comment>> findByPostId(UUID id);

}
