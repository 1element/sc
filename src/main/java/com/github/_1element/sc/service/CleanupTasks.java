package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler component to perform clean up tasks.
 */
@Component
public class CleanupTasks {

  private SurveillanceImageRepository imageRepository;

  @Value("${sc.archive.cleanup.enabled:false}")
  private Boolean isCleanupEnabled;

  @Value("${sc.archive.cleanup.keep:72}")
  private Integer keepHours;

  @Value("${sc.image.storage-dir}")
  private String imageStorageDirectory;

  private static final String THUMBNAIL_PREFIX = "thumbnail.";

  private static final String CRON_EVERY_DAY_AT_4_AM = "0 0 4 * * *";
  
  private static final Logger LOG = LoggerFactory.getLogger(CleanupTasks.class);

  @Autowired
  public CleanupTasks(SurveillanceImageRepository imageRepository) {
    this.imageRepository = imageRepository;
  }

  /**
   * Remove archived images older than X hours.
   */
  @Scheduled(cron=CRON_EVERY_DAY_AT_4_AM)
  public void cleanupArchive() {
    if (!Boolean.TRUE.equals(isCleanupEnabled)) {
      LOG.info("Task to remove old archived images not enabled in configuration. Do nothing.");
      return;
    }

    LocalDateTime removeBefore = LocalDateTime.now().minusHours(keepHours);
    List<SurveillanceImage> images = imageRepository.getArchivedImagesToCleanup(removeBefore);

    int numberOfImages = 0;
    for (SurveillanceImage image : images) {
      Path imageFilePath = Paths.get(imageStorageDirectory + image.getFileName());
      Path thumbnailFilePath = Paths.get(imageStorageDirectory + THUMBNAIL_PREFIX + image.getFileName());
      try {
        Files.delete(imageFilePath);
        Files.delete(thumbnailFilePath);
        numberOfImages++;
      } catch (Exception e) {
        LOG.warn("Exception occurred while removing old archived image/thumbnail '{}'/'{}', cause '{}'",
            imageFilePath.toString(), thumbnailFilePath.toString(), e.getMessage());
      }
      imageRepository.delete(image);
    }

    LOG.info("Successfully removed {} archived images.", numberOfImages);
  }

}
