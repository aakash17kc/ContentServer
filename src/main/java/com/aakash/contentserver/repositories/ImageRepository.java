package com.aakash.contentserver.repositories;

import com.aakash.contentserver.entities.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Image entity.

 */
@Repository
public interface ImageRepository extends FileRepository<Image> {
  /**
   * Find image by id.
   * @param id UUID
   * @return Optional of Image
   */
  Optional<Image> findById(UUID id);
}
