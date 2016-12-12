package com.github._1element.sc.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * File service class.
 */
@Service
public class FileService {

  @Value("${sc.image.valid-extensions}")
  private String[] validExtensions;

  /**
   * Returns true if file has valid extension.
   *
   * @param filename filename to check
   * @return true if extension is valid
   */
  public boolean hasValidExtension(String filename) {
    if (validExtensions == null) {
      return true;
    }

    for (String validExtension : validExtensions) {
      if (StringUtils.endsWithIgnoreCase(filename, validExtension)) {
        return true;
      }
    }

    return false;
  }

}
