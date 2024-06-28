package com.aakash.contentserver.processors;

import com.aakash.contentserver.constants.ImageConstants;
import com.aakash.contentserver.entities.Image;
import com.aakash.contentserver.enums.ActivityType;
import com.aakash.contentserver.impl.ImageFunctionImpl;
import com.aakash.contentserver.impl.ImageResizeConfigurationImpl;
import com.aakash.contentserver.impl.ImageSupportedTypeImpl;
import com.aakash.contentserver.impl.S3ProcessorImpl;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * ImageProcessor class to resize the image and upload to S3.
 */
@Component
public class ImageProcessor {

  private final ImageSupportedTypeImpl imageSupportedTypeImpl;

  private final S3ProcessorImpl s3ProcessorImpl;

  private final Logger logger;

  private final ImageResizeConfigurationImpl imageResizeConfiguration;
  private final ImageFunctionImpl imageFunctionImpl;


  public ImageProcessor(ImageSupportedTypeImpl imageSupportedTypeImpl, S3ProcessorImpl s3ProcessorImpl,
                        ImageResizeConfigurationImpl imageResizeConfiguration, ImageFunctionImpl imageFunctionImpl) {
    this.imageSupportedTypeImpl = imageSupportedTypeImpl;
    this.s3ProcessorImpl = s3ProcessorImpl;
    this.imageResizeConfiguration = imageResizeConfiguration;
    this.imageFunctionImpl = imageFunctionImpl;
    this.logger = LoggerFactory.getLogger(ImageProcessor.class);
  }

  /**
   * Method to resize the image and upload to S3.
   * The resize configuration is fetched from resize_config.json file present in resources folder based on the activity type.
   * There are different configurations for different activities like post and comment.
   * To make changes to the resize configuration, update the resize_config.json file.
   * This method is asynchronous and runs on a separate thread. The Async configuration is present in the AsyncConfig class.
   * If multiple services need to use this configuration, it can be saved to the database and fetched from there.
   *
   * @param filePath     File to be resized.
   * @param image        Image object to set the type.
   * @param activityType Content type to fetch the configuration.
   * @param <T>          Image type.
   * @throws IOException Exception if there is an issue with the file.
   */
  @Async("taskExecutor")
  public <T extends Image> void resizeImageAndUploadToS3(File filePath, T image, ActivityType activityType) throws IOException {

    // Fetching image configuration for the ActivityType.
    // In this case, it's the post activity.
    JsonNode imageConfig = getImageConfigurationByActivity(activityType);
    logger.info("Resizing image for postId: {}", image.getPostId());

    int width = getImageWidth(imageConfig);
    int height = getImageHeight(imageConfig);
    String format = getImageFormat(imageConfig);
    String destinationFileName = image.getLocation();
    // Resizing the image.
    ByteArrayOutputStream imageOutputStream = imageFunctionImpl.resizeImage(filePath, width, height, format);
    // Uploading the image to S3.
    logger.info("Uploading resized image to S3 for postId: {}", image.getPostId());
    s3ProcessorImpl.uploadFileAsByteStream(imageOutputStream.toByteArray(), destinationFileName);
  }
  
  /**
   * Method to upload the original image to S3.
   *
   * @param filePath            File to be uploaded
   * @param image               Image object to set the type.
   * @param destinationFileName The destination name of the file to be uploaded
   * @param <T>                 File type.
   * @throws IOException Exception if there is an issue with the file.
   */
  @Async("taskExecutor")
  public <T extends Image> void uploadOriginalImageToS3(File filePath, T image, String destinationFileName) throws IOException {
    logger.info("Uploading original image to S3 for postId: {}", image.getPostId());
    s3ProcessorImpl.uploadFileAsByteStream(FileUtils.readFileToByteArray(filePath), destinationFileName);
    logger.info("Original image uploaded successfully for postId: " + image.getPostId());
  }
  
  public <T extends Image> byte[] downloadImageFromS3(T image) throws IOException {
    logger.info("Downloading resized image from S3 for postId: {}", image.getPostId());
    return s3ProcessorImpl.downloadFile(image);

  }

  /**
   * Method to validate the image type.
   *
   * @param file Image file to validate.
   */
  public void validateSupportedImageType(MultipartFile file) {
    imageSupportedTypeImpl.getType(file);
  }

  private int getImageWidth(JsonNode jsonNode) {
    return jsonNode.get(ImageConstants.WIDTH).asInt();
  }

  private int getImageHeight(JsonNode jsonNode) {
    return jsonNode.get(ImageConstants.HEIGHT).asInt();
  }

  private String getImageFormat(JsonNode jsonNode) {
    return jsonNode.get(ImageConstants.TYPE).asText();
  }
  
  /**
   * Method to get the image configuration based on the activity type - Post or Comment.
   *
   * @param activityType Activity type to fetch the configuration.
   * @return JsonNode Image configuration.
   */
  private JsonNode getImageConfigurationByActivity(ActivityType activityType) {
    return imageResizeConfiguration.getImageConfigurationByActivity(activityType);
  }
  
  public String getResizedLocation(String postId, String extension) {
    return ImageConstants.COMPRESSED_LOCATION + postId + "." + extension;
  }
  
  public String getOriginalLocation(String postId, String extension) {
    return ImageConstants.ORIGINAL_LOCATION + postId + "." + extension;
  }
}
