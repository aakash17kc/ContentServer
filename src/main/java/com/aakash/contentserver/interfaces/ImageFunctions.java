package com.aakash.contentserver.interfaces;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Image functions interface. I've added additional method to highlight the different use cases there can be.
 */
@Component
public interface ImageFunctions {

  ByteArrayOutputStream resizeImage(MultipartFile imageInputStream, int width, int height, String outputFormat) throws IOException;

  ByteArrayOutputStream compressImage(MultipartFile file, float quality, String outputFormat) throws IOException;

  ByteArrayOutputStream scaleImage(MultipartFile file, double scaleFactor, String outputFormat) throws IOException;
}
