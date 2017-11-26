package com.github._1element.sc.service; //NOSONAR

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github._1element.sc.properties.ImageThumbnailProperties;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

/**
 * Thumbnail service.
 */
@Service
public class ThumbnailService {

  private ImageThumbnailProperties imageThumbnailProperties;

  private static final Logger LOG = LoggerFactory.getLogger(ThumbnailService.class);

  @Autowired
  public ThumbnailService(ImageThumbnailProperties imageThumbnailProperties) {
    this.imageThumbnailProperties = imageThumbnailProperties;
  }

  /**
   * Generates a thumbnail for the given path.
   *
   * @param path the file path to create a thumbnail for
   */
  public void createThumbnail(Path path) {
    try {
      Thumbnails.of(path.toFile())
        .size(imageThumbnailProperties.getWidth(), imageThumbnailProperties.getHeight())
        .outputQuality(imageThumbnailProperties.getQuality())
        .toFiles(Rename.PREFIX_DOT_THUMBNAIL);
    } catch (IOException exception) {
      LOG.warn("Unable to generate thumbnail: {}", exception.getMessage());
    }
  }

}
