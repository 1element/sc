package com.github._1element.sc.domain.pushnotification; //NOSONAR

/**
 * Factory to create a push notification client adapter.
 */
public interface PushNotificationClientFactory {

  /**
   * Returns the push notification client.
   * 
   * @param name the client adapter name
   * @return the push notification client
   */
  public PushNotificationClient getClient(String name);

}
