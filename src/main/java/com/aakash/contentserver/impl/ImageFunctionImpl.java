package com.aakash.contentserver.impl;

import com.aakash.contentserver.interfaces.ImageFunctions;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class ImageFunctionImpl implements ImageFunctions {
  
  public ImageFunctionImpl() {
  }
  
  @Override
  public ByteArrayOutputStream resizeImage(File filePath, int width, int height, String outputFormat) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(FileUtils.openInputStream(filePath))
        .size(width, height)
        .outputFormat(outputFormat)
        .toOutputStream(outputStream);
    return outputStream;
  }
  
  @Override
  public ByteArrayOutputStream compressImage(File filePath, float quality, String outputFormat) throws IOException {
    
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(FileUtils.openInputStream(filePath))
        .outputFormat(outputFormat)
        .outputQuality(quality)
        .toOutputStream(outputStream);
    return outputStream;
  }
  
  @Override
  public ByteArrayOutputStream scaleImage(File filePath, double scaleFactor, String outputFormat) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(FileUtils.openInputStream(filePath))
        .outputFormat(outputFormat)
        .scale(scaleFactor)
        .toOutputStream(outputStream);
    return outputStream;
  }
}
