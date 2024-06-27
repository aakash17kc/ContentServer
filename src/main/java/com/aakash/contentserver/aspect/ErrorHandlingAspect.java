package com.aakash.contentserver.aspect;


import com.aakash.contentserver.configuration.ApiError;
import com.aakash.contentserver.exceptions.*;
import com.aakash.contentserver.utils.JsonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;


@ControllerAdvice
public class ErrorHandlingAspect {
  /**
   * Handles BadRequestException and returns a 400 BAD_REQUEST response with the error message.
   * Usually thrown when the request received is invalid.
   * @param ex BadRequestException
   * @return ResponseEntity with 400 status and error message
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<String> handleServiceException(BadRequestException ex) {
    return ResponseEntity
        .badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonUtils.getErrorBody(ex.getMessage()));
  }

  /**
   * Handles ContentServerException and returns a 500 internal server response with the error message.
   * Usually thrown when an exception occurs while processing the request.
   * @param ex MethodArgumentNotValidException
   * @return ResponseEntity with 500 status and error message
   */
  @ExceptionHandler(ContentServerException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleContentServerException(ContentServerException ex) {
    return ResponseEntity
        .internalServerError()
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonUtils.getErrorBody(ex.getMessage()));
  }

  /**
   * Handles MultipartException and returns a 400 BAD_REQUEST response with the error message.
   * Usually thrown when the create post request does not contain an image file.
   * @param ex MethodArgumentNotValidException
   * @return ResponseEntity with 400 status and error message
   */
  @ExceptionHandler(MultipartException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleMultipartException(MultipartException ex) {
    String exceptionMessage = JsonUtils.getErrorBody(String.format("Image is required to create a post %s", ex.getMessage()));
    return ResponseEntity
        .badRequest()
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonUtils.getErrorBody(exceptionMessage));
  }

  /**
   * Handles MissingServletRequestParameterException and returns a 400 BAD_REQUEST response with the error message.
   * Usually thrown when the request received doesn't contain the required parameters.
   * @param ex MethodArgumentNotValidException
   * @param request  WebRequest
   * @return ResponseEntity with 400 status and error message
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiError> handleMissingServletRequestParameterExceptions(MissingServletRequestParameterException ex, WebRequest request) {
    String errorMessage = String.format("Missing required parameter: %s. Please include the parameter and try again.", ex.getParameterName());
    ApiError apiError = new ApiError("Required Parameter Missing", errorMessage);
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles EntityNotFoundException and returns a 404 NOT_FOUND response with the error message.
   * Usually thrown when the entity being requested is not found.
   * @param ex EntityNotFoundException
   * @return ResponseEntity with 400 status and error message
   */
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<String> handleEntityNotFoundExceptions(EntityNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonUtils.getErrorBody(ex.getMessage()));
  }

  /**
   *  Handles RateLimitException and returns a 429 TOO_MANY_REQUESTS  response with the error message.
   * @param ex RateLimitException
   * @return ResponseEntity with 429 status and error message
   */
  @ExceptionHandler(RateLimitException.class)
  public ResponseEntity<String> handleRateLimitException(RateLimitException ex) {
    return ResponseEntity
        .status(HttpStatus.TOO_MANY_REQUESTS)
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonUtils.getErrorBody(ex.getMessage()));
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonUtils.getErrorBody("Invalid request body. Please refer to the ServiceDescription.md"));
  }
  @ExceptionHandler(EntityNotValidException.class)
  public ResponseEntity<String> handleEntityNotValidException(EntityNotValidException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(ex.getMessage());
  }
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<String> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonUtils.getErrorBody(ex.getMessage()));
  }
  @ExceptionHandler(MultipleFilesUploadException.class)
  public ResponseEntity<String> handleMultipleFilesUploadException(MultipleFilesUploadException ex) {
    return  ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(JsonUtils.getErrorBody(ex.getMessage()));
  }
}
