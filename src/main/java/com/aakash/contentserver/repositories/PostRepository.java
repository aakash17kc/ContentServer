package com.aakash.contentserver.repositories;

import com.aakash.contentserver.entities.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

/**
 * Repository for Post entity.
 */
@Repository
public interface PostRepository extends ContentRepository<Post> {
  /**
   * Find all posts by comments count in descending order and created at in descending order
   *
   * @param pageable Pageable
   * @return Page of posts
   */
  public Page<Post> findAllByOrderByCommentsCountDescCreatedAtDesc(Pageable pageable);

}
