package com.github._1element.sc.domain.pushnotification; //NOSONAR

import com.github._1element.sc.exception.PushNotificationClientException;

/**
 * Interface for all push notification clients.
 */
public interface PushNotificationClient {

  void sendMessage(String title, String text) throws PushNotificationClientException;

}
