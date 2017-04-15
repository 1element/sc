package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.PushNotificationSetting;
import com.github._1element.sc.dto.CameraPushNotificationSettingResult;
import com.github._1element.sc.events.PushNotificationEvent;
import com.github._1element.sc.properties.PushNotificationProperties;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.PushNotificationSettingRepository;

import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverException;
import net.pushover.client.PushoverMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service to send push notifications and retrieve configuration settings.
 * Uses pushover.net API.
 */
@Service
public class PushNotificationService {

  private PushNotificationSettingRepository pushNotificationSettingRepository;

  private CameraRepository cameraRepository;
  
  private PushoverClient pushoverClient;

  private PushNotificationProperties properties;

  private MessageSource messageSource;

  private static Map<String, Instant> lastPushNotification = new HashMap<>();

  private static final String MESSAGE_PROPERTIES_PUSH_TITLE = "push-notification.title";

  private static final String MESSAGE_PROPERTIES_PUSH_MESSAGE = "push-notification.message";

  private static final Logger LOG = LoggerFactory.getLogger(PushNotificationService.class);

  @Autowired
  public PushNotificationService(PushNotificationSettingRepository pushNotificationSettingRepository,
                                 CameraRepository cameraRepository, PushoverClient pushoverClient, 
                                 PushNotificationProperties properties,
                                 MessageSource messageSource) {
    this.pushNotificationSettingRepository = pushNotificationSettingRepository;
    this.cameraRepository = cameraRepository;
    this.pushoverClient = pushoverClient;
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

    PushoverMessage.Builder pushoverMessageBuilder = PushoverMessage.builderWithApiToken(properties.getApiToken())
      .setUserId(properties.getUserToken())
      .setTitle(title)
      .setMessage(text);
    
    if (StringUtils.isNotBlank(url)) {
      pushoverMessageBuilder.setUrl(url);
    }

    PushoverMessage pushoverMessage = pushoverMessageBuilder.build();

    try {
      pushoverClient.pushMessage(pushoverMessage);
      LOG.debug("Push notification with title '{}' was sent.", pushoverMessage.getTitle());
    } catch (PushoverException e) {
      LOG.error("Could not send push notification '{}', exception was: {}", title, e.getMessage());
    }
  }

  /**
   * Handles push notification events.
   * Will perform checks if notification should be sent or if throttle is applied or disabled by configuration.
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

    // throttle if group time is not reached
    Instant currentInstant = Instant.now();
    Instant lastPushNotificationInstant = lastPushNotification.get(camera.getId());
    if ((lastPushNotificationInstant != null) && (properties.getGroupTime() > 0)) {
      long minutesBetween = ChronoUnit.MINUTES.between(lastPushNotificationInstant, currentInstant);
      if (minutesBetween < properties.getGroupTime()) {
        LOG.debug("Push notification was not sent. Last push notification for camera '{}' was {} minute(s) ago. Group time is {} minute(s).",
            camera.getId(), minutesBetween, properties.getGroupTime());
        return;
      }
    }

    // send push message
    String title = messageSource.getMessage(MESSAGE_PROPERTIES_PUSH_TITLE, new Object[]{camera.getName()}, LocaleContextHolder.getLocale());
    String message = messageSource.getMessage(MESSAGE_PROPERTIES_PUSH_MESSAGE, new Object[]{camera.getName(), LocalDateTime.now().toString()}, LocaleContextHolder.getLocale());
    sendMessage(title, message, properties.getUrl());

    // save timestamp of this run
    lastPushNotification.put(camera.getId(), currentInstant);
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
      PushNotificationSetting pushNotificationSetting = pushNotificationSettingRepository.findByCameraId(camera.getId());
      if (pushNotificationSetting == null) {
        pushNotificationSetting = new PushNotificationSetting(camera.getId(), false);
        pushNotificationSettingRepository.save(pushNotificationSetting);
      }
      result.add(new CameraPushNotificationSettingResult(camera, pushNotificationSetting));
    }

    return result;
  }

}
