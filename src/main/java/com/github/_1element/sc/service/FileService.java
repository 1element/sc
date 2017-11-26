package com.github._1element.sc.service; //NOSONAR

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github._1element.sc.properties.ImageProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * File service class.
 */
@Service
public class FileService {

  private ImageProperties imageProperties;

  private static final String DATE_TIME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";

  private static final String SEPARATOR = "-";

  @Autowired
  public FileService(ImageProperties imageProperties) {
    this.imageProperties = imageProperties;
  }

  /**
   * Returns true if file has valid extension.
   * If there are no valid extensions configured, this will always
   * evaluate to true.
   *
   * @param filename filename to check
   * @return true if extension is valid
   */
  public boolean hasValidExtension(String filename) {
    if (imageProperties.getValidExtensions() == null) {
      return true;
    }

    for (String validExtension : imageProperties.getValidExtensions()) {
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
    String timestamp = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(LocalDateTime.now());

    return timestamp + SEPARATOR + RandomStringUtils.randomAlphabetic(7);
  }

  /**
   * Creates a new {@link InputStream} for a given path.
   *
   * @param path the path to the file to open
   * @return a new input stream
   * @throws IOException
   */
  public InputStream createInputStream(Path path) throws IOException {
    return Files.newInputStream(path);
  }

  /**
   * Deletes a file.
   *
   * @param path the path to the file to delete
   * @throws IOException
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

  /**
   * Moves a file.
   * This is just a wrapper for the static {@link Files#move(Path, Path, CopyOption...)} method to simplify unit testing.
   *
   * @param sourcePath the source to be moved
   * @param destinationPath the destination path
   * @throws IOException
   */
  public void moveFile(Path sourcePath, Path destinationPath) throws IOException {
    Files.move(sourcePath, destinationPath);
  }

}
