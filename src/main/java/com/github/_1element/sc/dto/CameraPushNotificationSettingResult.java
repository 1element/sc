package com.github._1element.sc.dto;

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.PushNotificationSetting;

/**
 * Mapping push notification settings to camera.
 */
public class CameraPushNotificationSettingResult {

  private Camera camera;

  private PushNotificationSetting pushNotificationSetting;

  public CameraPushNotificationSettingResult(Camera camera, PushNotificationSetting pushNotificationSetting) {
    this.camera = camera;
    this.pushNotificationSetting = pushNotificationSetting;
  }

  public Camera getCamera() {
    return camera;
  }

  public PushNotificationSetting getPushNotificationSetting() {
    return pushNotificationSetting;
  }

}
