package com.aakash.contentserver.impl;

import com.aakash.contentserver.interfaces.ImageFunctions;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageFunctionImpl implements ImageFunctions {

  public ImageFunctionImpl() {
  }

  @Override
  public ByteArrayOutputStream resizeImage(MultipartFile file, int width, int height, String outputFormat) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(file.getInputStream())
        .size(width, height)
        .outputFormat(outputFormat)
        .toOutputStream(outputStream);
    return outputStream;
  }

  @Override
  public ByteArrayOutputStream compressImage(MultipartFile file, float quality, String outputFormat) throws IOException {

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(file.getInputStream())
        .outputFormat(outputFormat)
        .outputQuality(quality)
        .toOutputStream(outputStream);
    return outputStream;
  }

  @Override
  public ByteArrayOutputStream scaleImage(MultipartFile file, double scaleFactor, String outputFormat) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(file.getInputStream())
        .outputFormat(outputFormat)
        .scale(scaleFactor)
        .toOutputStream(outputStream);
    return outputStream;
  }
}
