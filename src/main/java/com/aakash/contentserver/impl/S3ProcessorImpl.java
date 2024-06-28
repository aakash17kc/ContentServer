package com.aakash.contentserver.impl;

import com.aakash.contentserver.constants.ImageConstants;
import com.aakash.contentserver.constants.S3Constants;
import com.aakash.contentserver.entities.FileType;
import com.aakash.contentserver.interfaces.S3Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * S3 Processor class to upload and download files from S3.
 */
@Service
public class S3ProcessorImpl implements S3Processor {
  
  private final S3Client s3Client;
  private final Logger logger;
  
  public S3ProcessorImpl(S3Client s3Client) {
    this.s3Client = s3Client;
    this.logger = LoggerFactory.getLogger(S3ProcessorImpl.class);
  }
  
  /**
   * Uploads image as byte stream to S3
   * The image is uploaded in parts of 5MB each
   * This method leverages the Multipart upload feature of S3 which allows uploading large files in parts
   * AWS SDK v2 has built in retry logic for uploads, so we don't need to implement it.
   *
   * @param imageBytes          The image as byte array
   * @param destinationFileName The destination name of the file to be uploaded
   */
  @Override
  public void uploadFileAsByteStream(byte[] imageBytes, String destinationFileName) {
    logger.info("Uploading file to S3: {}", destinationFileName);
    int partSize = ImageConstants.PART_SIZE; // 5 MB chunk
    byte[] buffer = new byte[partSize];
    
    // If the image is less than 5MB, upload the image directly.
    if (imageBytes.length < partSize) {
      try {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(S3Constants.BUCKET_NAME)
            .key(destinationFileName)
            .build();
        
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
        logger.info("File uploaded to S3: {}", destinationFileName);
        return;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
        .bucket(S3Constants.BUCKET_NAME)
        .key(destinationFileName)
        .build();
    
    CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);
    String uploadId = response.uploadId();
    
    List<CompletedPart> completedParts = new ArrayList<>();
    int fileLength = imageBytes.length;
    int partNumber = 1;
    
    for (int offset = 0; offset < fileLength; offset += partSize) {
      int currentPartSize = Math.min(partSize, fileLength - offset);
      System.arraycopy(imageBytes, offset, buffer, 0, currentPartSize);
      
      UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
          .bucket(S3Constants.BUCKET_NAME)
          .key(destinationFileName)
          .uploadId(uploadId)
          .partNumber(partNumber)
          .build();
      
      // Upload part
      String etag = s3Client.uploadPart(uploadPartRequest, RequestBody.fromBytes(buffer)).eTag();
      completedParts.add(CompletedPart.builder().partNumber(partNumber).eTag(etag).build());
      partNumber++;
    }
    
    CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
        .bucket(S3Constants.BUCKET_NAME)
        .key(destinationFileName)
        .uploadId(uploadId)
        .multipartUpload(CompletedMultipartUpload.builder()
            .parts(completedParts)
            .build())
        .build();
    
    s3Client.completeMultipartUpload(completeMultipartUploadRequest);
    logger.info("File uploaded to S3: {}", destinationFileName);
  }
  
  /**
   * method to download file from S3 and return as byte array.
   * For this specific implementation, the file is downloaded into buffer and then returned.
   * This can cause memory issues if the file is too large or if there are too many concurrent requests.
   * The memory issues can be solved creating a FileOutputStream of a file location
   * and writing the file in chunks. Doing so will consume less memory, but more CPU.
   *
   * @param fileType FileType
   * @return byte[] of the image
   * @throws RuntimeException Exception
   */
  @Override
  public byte[] downloadFile(FileType fileType) throws RuntimeException {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(fileType.getBucketName())
        .key(fileType.getLocation())
        .build();
    try (ResponseInputStream<GetObjectResponse> s3ObjectInputStream = s3Client.getObject(getObjectRequest);
         ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
      
      byte[] dataChunk = new byte[ImageConstants.PART_SIZE];
      int bytesRead;
      while ((bytesRead = s3ObjectInputStream.read(dataChunk)) != -1) {
        buffer.write(dataChunk, 0, bytesRead);
      }
      return buffer.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException("Failed to download image from S3 for file : " + fileType.getLocation(), e);
    }
  }
}
