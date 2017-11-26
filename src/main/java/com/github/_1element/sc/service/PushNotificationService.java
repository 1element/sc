package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.PushNotificationSetting;
import com.github._1element.sc.domain.pushnotification.PushNotificationClient;
import com.github._1element.sc.domain.pushnotification.PushNotificationClientFactory;
import com.github._1element.sc.dto.CameraPushNotificationSettingResult;
import com.github._1element.sc.events.PushNotificationEvent;
import com.github._1element.sc.exception.PushNotificationClientException;
import com.github._1element.sc.properties.PushNotificationProperties;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.PushNotificationSettingRepository;
import com.google.common.annotations.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to send push notifications and retrieve configuration settings.
 */
@Service
public class PushNotificationService {

  private PushNotificationClientFactory pushNotificationClientFactory;

  private PushNotificationSettingRepository pushNotificationSettingRepository;

  private CameraRepository cameraRepository;

  private PushNotificationProperties properties;

  private MessageSource messageSource;

  private static Map<String, Instant> lastPushNotification = new HashMap<>();

  private static final String MESSAGE_PROPERTIES_PUSH_TITLE = "push-notification.title";

  private static final String MESSAGE_PROPERTIES_PUSH_MESSAGE = "push-notification.message";

  private static final String QUERY_PARAM_CAMERA = "camera";

  private static final Logger LOG = LoggerFactory.getLogger(PushNotificationService.class);

  /**
   * Constructs a push notification service.
   *
   * @param pushNotificationClientFactory the factory to create a specific push notification client
   * @param pushNotificationSettingRepository the setting repository
   * @param cameraRepository the camera repository
   * @param properties the configured push notification specific properties
   * @param messageSource the message source for localization
   */
  @Autowired
  public PushNotificationService(PushNotificationClientFactory pushNotificationClientFactory,
                                 PushNotificationSettingRepository pushNotificationSettingRepository,
                                 CameraRepository cameraRepository,
                                 PushNotificationProperties properties,
                                 MessageSource messageSource) {
    this.pushNotificationClientFactory = pushNotificationClientFactory;
    this.pushNotificationSettingRepository = pushNotificationSettingRepository;
    this.cameraRepository = cameraRepository;
    this.properties = properties;
    this.messageSource = messageSource;
  }

  /**
   * Send push message.
   *
   * @param title message title
   * @param text message text
   */
  public void sendMessage(String title, String text) {
    sendMessage(title, text, null);
  }

  /**
   * Send push message.
   *
   * @param title message title
   * @param text message text
   * @param url optional url to include in message
   */
  public void sendMessage(String title, String text, String url) {
    if (!properties.isEnabled()) {
      LOG.debug("Push notifications are disabled.");
      return;
    }

    PushNotificationClient pushNotificationClient = pushNotificationClientFactory.getClient(properties.getAdapter());

    try {
      pushNotificationClient.sendMessage(title, text, url);
      LOG.debug("Push notification with title '{}' was sent.", title);
    } catch (PushNotificationClientException exception) {
      LOG.error("Push notification with title '{}' was not sent. {}", title, exception.getMessage());
    }
  }

  /**
   * Handles push notification events.
   * Will perform checks if notification should be sent or not.
   *
   * @param pushNotificationEvent push notification event
   */
  @EventListener
  public void handlePushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
    // check if notifications are enabled for this camera
    Camera camera = pushNotificationEvent.getCamera();
    PushNotificationSetting setting = pushNotificationSettingRepository.findByCameraId(camera.getId());
    if (setting == null || (!setting.isEnabled())) {
      LOG.debug("Push notifications are disabled for camera '{}'.", camera.getId());
      return;
    }

    // check if rate limit is reached
    if (hasRateLimitReached(camera.getId())) {
      return;
    }

    // send push message
    String title = messageSource.getMessage(MESSAGE_PROPERTIES_PUSH_TITLE, new Object[]{camera.getName()},
        LocaleContextHolder.getLocale());
    String message = messageSource.getMessage(MESSAGE_PROPERTIES_PUSH_MESSAGE, new Object[]{camera.getName(),
        LocalDateTime.now().toString()}, LocaleContextHolder.getLocale());
    String url = buildCameraUrl(camera.getId());
    sendMessage(title, message, url);
  }

  /**
   * Retrieve push notification configuration for each camera.
   *
   * @return camera push notification settings
   */
  public List<CameraPushNotificationSettingResult> getAllSettings() {
    List<CameraPushNotificationSettingResult> result = new ArrayList<>();
    List<Camera> allCameras = cameraRepository.findAll();

    for (Camera camera : allCameras) {
      PushNotificationSetting pushNotificationSetting =
          pushNotificationSettingRepository.findByCameraId(camera.getId());
      if (pushNotificationSetting == null) {
        pushNotificationSetting = new PushNotificationSetting(camera.getId(), false);
        pushNotificationSettingRepository.save(pushNotificationSetting);
      }
      result.add(new CameraPushNotificationSettingResult(camera, pushNotificationSetting));
    }

    return result;
  }

  /**
   * Build URL for given cameraId.
   * This will use the configured base URL for push notifications and
   * append camera specific parameters.
   *
   * @param cameraId the camera ID to build URL for
   * @return the camera specific URL
   */
  @VisibleForTesting
  String buildCameraUrl(String cameraId) {
    String baseUrl = properties.getUrl();
    if (StringUtils.isBlank(baseUrl)) {
      return null;
    }

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl);
    uriComponentsBuilder.queryParam(QUERY_PARAM_CAMERA, cameraId);

    return uriComponentsBuilder.build().toUriString();
  }

  /**
   * Perform rate limitation (throttle).
   * Push notification should not be sent if the time period between this
   * and the last run is less than the configured group time.
   *
   * @param cameraId the camera id to perform check for
   * @return true if rate limit is reached
   */
  @VisibleForTesting
  synchronized boolean hasRateLimitReached(String cameraId) {
    Instant currentInstant = Instant.now();
    Instant lastPushNotificationInstant = lastPushNotification.get(cameraId);
    if ((lastPushNotificationInstant != null) && (properties.getGroupTime() > 0)) {
      long minutesBetween = ChronoUnit.MINUTES.between(lastPushNotificationInstant, currentInstant);
      if (minutesBetween < properties.getGroupTime()) {
        LOG.debug("Push notification was not sent. Last push notification for camera '{}' was {} minute(s) ago. "
            + "Group time is {} minute(s).", cameraId, minutesBetween, properties.getGroupTime());
        return true;
      }
    }

    // save timestamp of this run
    lastPushNotification.put(cameraId, currentInstant);

    return false;
  }

}
