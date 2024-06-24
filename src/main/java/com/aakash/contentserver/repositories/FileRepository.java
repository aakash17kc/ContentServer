package com.aakash.contentserver.repositories;

import com.aakash.contentserver.entities.FileType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileRepository<T extends FileType> extends MongoRepository<T, UUID> {
  /**
   * Find entity of file type by id.
   * @param id UUID
   * @return Optional of entity
   */
  public Optional<T> findById(UUID id);

}
