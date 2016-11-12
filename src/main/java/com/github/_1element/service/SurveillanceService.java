package com.github._1element.service;

import com.github._1element.domain.Camera;
import com.github._1element.domain.SurveillanceImage;
import com.github._1element.events.ImageReceivedEvent;
import com.github._1element.repository.CameraRepository;
import com.github._1element.repository.SurveillanceImageRepository;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Main surveillance service class.
 */
@Service
public class SurveillanceService {

  @Autowired
  private SurveillanceImageRepository imageRepository;

  @Autowired
  private CameraRepository cameraRepository;

  @Value("${sc.image.storage-dir}")
  private String imageStorageDirectory;

  @Value("${sc.image.thumbnail.width:200}")
  private int thumbnailWidth;

  @Value("${sc.image.thumbnail.height:200}")
  private int thumbnailHeight;

  @Value("${sc.image.thumbnail.quality:0.8}")
  private double thumbnailQuality;

  private static final String SEPARATOR = "-";

  private static final Logger LOG = LoggerFactory.getLogger(SurveillanceService.class);

  /**
   * Event listener for image received events.
   * Will create thumbnail and database entry.
   *
   * @param imageReceivedEvent image received event
   * @throws IOException
   */
  @EventListener
  public void handleImageReceivedEvent(ImageReceivedEvent imageReceivedEvent) throws IOException {
    LOG.info("ImageReceivedEvent for '{}'", imageReceivedEvent.getFileName());

    // move file from incoming ftp directory to final storage directory
    File sourceFile = new File(imageReceivedEvent.getFileName());
    String destinationFileName = imageStorageDirectory + imageReceivedEvent.getSource().getId() + SEPARATOR + sourceFile.getName();
    File destinationFile = new File(destinationFileName);

    FileUtils.moveFile(sourceFile, destinationFile);

    // generate thumbnail
    Thumbnails.of(destinationFile)
      .size(thumbnailWidth, thumbnailHeight)
      .outputQuality(thumbnailQuality)
      .toFiles(Rename.PREFIX_DOT_THUMBNAIL);

    // store image information in database
    SurveillanceImage image = new SurveillanceImage(destinationFile.getName(), imageReceivedEvent.getSource().getId(), LocalDateTime.now());
    imageRepository.save(image);
  }

  /**
   * Returns page of surveillance images.
   *
   * @param camera      optional camera identifier
   * @param date        optional date
   * @param isArchive   archive flag
   * @param pageRequest page request
   * @return
   */
  public Page<SurveillanceImage> getImagesPage(Optional<String> camera, Optional<LocalDate> date, boolean isArchive, PageRequest pageRequest) {
    LocalDateTime startOfDay = null;
    LocalDateTime endOfDay = null;

    if (date.isPresent()) {
      startOfDay = date.get().atStartOfDay();
      endOfDay = LocalDateTime.of(date.get(), LocalTime.MAX);
    }

    if (camera.isPresent() && StringUtils.isNotBlank(camera.get())) {
      if (date.isPresent()) {
        return imageRepository.findAllByCameraIdAndReceivedAtBetweenAndArchived(camera.get(), startOfDay, endOfDay, isArchive, pageRequest);
      }

      return imageRepository.findAllByCameraIdAndArchived(camera.get(), isArchive, pageRequest);
    }

    if (date.isPresent()) {
      return imageRepository.findAllByReceivedAtBetweenAndArchived(startOfDay, endOfDay, isArchive, pageRequest);
    }

    return imageRepository.findAllByArchived(isArchive, pageRequest);
  }

  /**
   * Returns most recent image date.
   * This is only wanted if no date filter is present.
   *
   * @param images page of surveillance images
   * @param date   optional date filter
   * @return
   */
  public LocalDateTime getMostRecentImageDate(Page<SurveillanceImage> images, Optional<LocalDate> date) {
    if (!date.isPresent() && images != null && images.getContent() != null && images.getContent().size() > 0) {
      return images.getContent().get(0).getReceivedAt();
    }

    return null;
  }

  /**
   * Returns camera for given identifier, if found.
   *
   * @param camera optional camera identifier
   * @return
   */
  public Camera getCamera(Optional<String> camera) {
    if (camera.isPresent() && StringUtils.isNotBlank(camera.get())) {
      return cameraRepository.findById(camera.get());
    }

    return null;
  }

}
