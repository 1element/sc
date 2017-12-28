package com.github._1element.sc.dto; //NOSONAR

/**
 * Push notification setting resource.
 * REST projection for the internal {@link com.github._1element.sc.domain.PushNotificationSetting} entity.
 */
public class PushNotificationSettingResource {

  private String cameraId;

  private String cameraName;

  private boolean enabled;

  /**
   * Constructor.
   *
   * @param cameraId the camera identifier
   * @param cameraName the camera name
   * @param enabled the push notification status (enabled/disabled)
   */
  public PushNotificationSettingResource(String cameraId, String cameraName, boolean enabled) {
    this.cameraId = cameraId;
    this.cameraName = cameraName;
    this.enabled = enabled;
  }

  public String getCameraId() {
    return cameraId;
  }

  public String getCameraName() {
    return cameraName;
  }

  public boolean isEnabled() {
    return enabled;
  }

}
