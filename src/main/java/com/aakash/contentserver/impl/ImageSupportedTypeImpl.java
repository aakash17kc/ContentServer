package com.aakash.contentserver.impl;

import com.aakash.contentserver.enums.ImageType;
import com.aakash.contentserver.exceptions.BadRequestException;
import com.aakash.contentserver.interfaces.FileSupportedType;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageSupportedTypeImpl implements FileSupportedType {

  public ImageSupportedTypeImpl() {
  }

  /**
   * Method to get the file type of the image.
   * @param inputStream Image input stream.
   * @return Image type.
   */
  @Override
  public String getType(MultipartFile file) {
    String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
    if (!EnumUtils.isValidEnumIgnoreCase(ImageType.class, fileExtension)) {
      throw new BadRequestException(String.format("Only image files with extensions %s are supported ", EnumUtils.getEnumList(ImageType.class)));
    }
    return fileExtension;
  }
}
