package com.github._1element.sc.service;

import com.github._1element.sc.properties.PushNotificationProperties;
import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverException;
import net.pushover.client.PushoverMessage;
import net.pushover.client.PushoverRestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service to send push notifications.
 * Uses pushover.net API.
 */
@Service
public class PushNotificationService {

  private PushNotificationProperties properties;

  private static final Logger LOG = LoggerFactory.getLogger(PushNotificationService.class);

  @Autowired
  public PushNotificationService(PushNotificationProperties properties) {
    this.properties = properties;
  }

  /**
   * Send push message.
   *
   * @param title message title
   * @param text message text
   */
  public void sendMessage(String title, String text) {
    if (!properties.isEnabled()) {
      LOG.debug("Push notifications are disabled.");
      return;
    }

    PushoverClient pushoverClient = new PushoverRestClient();
    PushoverMessage pushoverMessage = PushoverMessage.builderWithApiToken(properties.getApiToken())
      .setUserId(properties.getUserToken())
      .setTitle(title)
      .setMessage(text)
      .build();

    try {
      pushoverClient.pushMessage(pushoverMessage);
    } catch (PushoverException e) {
      LOG.error("Could not send push notification '{}', exception was: {}", title, e.getMessage());
    }
  }

}
