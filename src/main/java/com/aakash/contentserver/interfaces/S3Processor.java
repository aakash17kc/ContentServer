package com.aakash.contentserver.interfaces;

import com.aakash.contentserver.entities.FileType;

/**
 * Interface for S3ProcessorImpl.
 */
public interface S3Processor {

  void uploadFileAsByteStream(byte[] imageBytes, String destinationFileName, FileType fileType);
  byte[] downloadFile(FileType fileType) throws RuntimeException;
}
