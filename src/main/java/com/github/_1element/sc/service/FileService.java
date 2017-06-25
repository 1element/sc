package com.github._1element.sc.service; //NOSONAR

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  /**
   * Creates a new {@link File} instance for the given path.
   * 
   * @param pathname the local path to the file
   * @return the file instance
   */
  public File createFile(String pathname) {
    return new File(pathname);
  }

  /**
   * Creates a new {@link FileInputStream} for a given file.
   * 
   * @param file the file to create the input stream for
   * @return the file input stream
   * @throws IOException
   */
  public FileInputStream createInputStream(File file) throws IOException {
    return new FileInputStream(file);
  }

  /**
   * Deletes a file.
   * 
   * @param path the path to the file to delete
   */
  public void delete(Path path) throws IOException {
    Files.delete(path);
  }

  /**
   * Converts a path string to a Path.
   * This is basically just a wrapper for {@link Paths#get(String, String...)} to simplify unit testing.
   * 
   * @param first the path string or initial part of the path string
   * @param more additional strings to be joined to form the path string
   * @return the resulting Path
   */
  public Path getPath(String first, String... more) {
    return Paths.get(first, more);
  }

}
