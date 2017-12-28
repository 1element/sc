package com.github._1element.sc.dto; //NOSONAR

/**
 * DTO used to update {@link com.github._1element.sc.domain.PushNotificationSetting}.
 */
public class PushNotificationSettingUpdateResource {

  private String cameraId;

  private boolean enabled;

  public String getCameraId() {
    return cameraId;
  }

  public void setCameraId(String cameraId) {
    this.cameraId = cameraId;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
