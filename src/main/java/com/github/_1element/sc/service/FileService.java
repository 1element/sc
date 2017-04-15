package com.github._1element.sc.service; //NOSONAR

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * File service class.
 */
@Service
public class FileService {

  @Value("${sc.image.valid-extensions}")
  private String[] validExtensions;

  private static final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

  private static final String SEPARATOR = "-";

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

  /**
   * Returns unique filename prefix.
   *
   * @return prefix
   */
  public String getUniquePrefix() {
    String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());

    return timestamp + SEPARATOR + RandomStringUtils.randomAlphabetic(7);
  }

}
