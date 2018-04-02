package com.github._1element.sc.service; //NOSONAR

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.events.ImageReceivedEvent;
import com.github._1element.sc.events.PushNotificationEvent;
import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.properties.ImageProperties;
import com.github._1element.sc.repository.SurveillanceImageRepository;

/**
 * Service to handle surveillance images.
 */
@Service
public class SurveillanceImageHandlerService {

  private final SurveillanceImageRepository imageRepository;

  private final FileService fileService;

  private final ThumbnailService thumbnailService;

  private final ImageProperties imageProperties;

  private final ApplicationEventPublisher eventPublisher;

  private static final String SEPARATOR = "-";

  private static final String IMAGE_EXTENSION = ".jpg";

  private static final Logger LOG = LoggerFactory.getLogger(SurveillanceImageHandlerService.class);

  /**
   * Constructor.
   *
   * @param imageRepository the image repository
   * @param fileService the file service
   * @param thumbnailService the thumbnail service
   * @param imageProperties the image properties
   */
  @Autowired
  public SurveillanceImageHandlerService(final SurveillanceImageRepository imageRepository,
                                         final FileService fileService,
                                         final ThumbnailService thumbnailService,
                                         final ImageProperties imageProperties,
                                         final ApplicationEventPublisher eventPublisher) {
    this.imageRepository = imageRepository;
    this.fileService = fileService;
    this.thumbnailService = thumbnailService;
    this.imageProperties = imageProperties;
    this.eventPublisher = eventPublisher;
  }

  /**
   * Event listener for image received events.
   * Will create thumbnail and database entry.
   *
   * @param imageReceivedEvent image received event
   */
  @Async
  @EventListener
  public void handleImageReceivedEvent(final ImageReceivedEvent imageReceivedEvent) {
    final String destinationFileName = populateDestinationFileName(imageReceivedEvent);
    final Path destinationPath = fileService.getPath(destinationFileName);

    try {
      // write file to disk
      fileService.write(destinationPath, imageReceivedEvent.getImage());

      // generate thumbnail
      thumbnailService.createThumbnail(destinationPath);

      // store image information in database
      final SurveillanceImage image = new SurveillanceImage(destinationPath.getFileName().toString(),
          imageReceivedEvent.getSource().getId(), LocalDateTime.now());
      imageRepository.save(image);

      LOG.info("New surveillance image '{}' was received.", image.getFileName());

      // publish events to invoke remote copy and push notification
      eventPublisher.publishEvent(new RemoteCopyEvent(destinationFileName));
      eventPublisher.publishEvent(new PushNotificationEvent(imageReceivedEvent.getSource()));
    } catch (final IOException exception) {
      LOG.error("Error while writing image to final storage directory: '{}'",
          exception.getMessage());
    }
  }

  /**
   * Populate destination file name for given image received event.
   *
   * @param imageReceivedEvent the image received event to build file name for
   * @return the destination file name
   */
  @VisibleForTesting
  String populateDestinationFileName(final ImageReceivedEvent imageReceivedEvent) {
    return imageProperties.getStorageDir() + fileService.getUniquePrefix() + SEPARATOR
        + imageReceivedEvent.getSource().getId() + IMAGE_EXTENSION;
  }

}
