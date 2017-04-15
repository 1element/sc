package com.github._1element.sc.events; //NOSONAR

import com.github._1element.sc.domain.Camera;

/**
 * Event to trigger push notification.
 */
public class PushNotificationEvent {

  private Camera camera;

  public PushNotificationEvent(Camera camera) {
    this.camera = camera;
  }

  public Camera getCamera() {
    return camera;
  }

}
