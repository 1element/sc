package com.github._1element.sc.service; //NOSONAR

import java.io.File;
import java.io.IOException;

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
   * Generates a thumbnail for the given file.
   * 
   * @param file the file to create a thumbnail for
   * @throws IOException 
   */
  public void createThumbnail(File file) {
    try {
      Thumbnails.of(file)
      .size(imageThumbnailProperties.getWidth(), imageThumbnailProperties.getHeight())
      .outputQuality(imageThumbnailProperties.getQuality())
      .toFiles(Rename.PREFIX_DOT_THUMBNAIL);
    } catch (IOException e) {
      LOG.warn("Unable to generate thumbnail: {}", e.getMessage());
    }
  }

}
