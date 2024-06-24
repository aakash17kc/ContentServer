package com.aakash.contentserver.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
  /**
   * Converts error message to json string
   * @param message
   * @return
   */
  public static String getErrorBody(String message) {
    return "{ \"error\": \"" + message + "\" }";
  }
}
