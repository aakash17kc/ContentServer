package com.aakash.contentserver.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileSupportedType {
  /**
   * Method to get the file extension.
   * @param file File to get the extension.
   * @return File extension.
   */
  public String getType(MultipartFile file);

  // Add additional methods to verify other file types.
}
