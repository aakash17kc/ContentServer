package com.aakash.contentserver.repositories;

import com.aakash.contentserver.entities.Content;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ContentRepository<T extends Content> extends MongoRepository<T, UUID> {
  /**
   * Find entity of content type by id.
   * @param id UUID
   * @return Optional of entity
   */
  public Optional<T> findById(UUID id);
  /**
   * Delete entity of content type by id.
   * @param id UUID
   */
  public void deleteById(UUID id);
}
