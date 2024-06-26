package com.aakash.contentserver.controller;

import com.aakash.contentserver.dto.ImageDTO;
import com.aakash.contentserver.services.ImageService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/v1/images")
public class ImageController {
  private final ImageService imageService;

  public ImageController(ImageService imageService) {
    this.imageService = imageService;

  }

  /**
   * Get an image by its id.
   * @param imageId
   * @return
   */
  @GetMapping("/{imageId}")
  public ResponseEntity<ImageDTO> getImage(@PathVariable String imageId) {
    ImageDTO imageDTO = imageService.getImage(imageId);
    return ResponseEntity
        .ok()
        .body(imageDTO);
  }

  /**
   * Get the content of an image by its id.
   * @param imageId
   * @return
   * @throws IOException
   */
  @GetMapping("/{imageId}/content")
  public ResponseEntity<?> getImageContent(@PathVariable String imageId) throws IOException {
    byte[] image = imageService.getImageContent(imageId);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.IMAGE_JPEG);
    return ResponseEntity
        .ok()
        .headers(headers)
        .body(image);
  }
}
