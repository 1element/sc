package com.github._1element.sc.dto; //NOSONAR

/**
 * Push notification setting resource.
 * REST projection for the internal {@link com.github._1element.sc.domain.PushNotificationSetting} entity.
 */
public class PushNotificationSettingResource {

  private final String cameraId;

  private final String cameraName;

  private final boolean enabled;

  /**
   * Constructor.
   *
   * @param cameraId the camera identifier
   * @param cameraName the camera name
   * @param enabled the push notification status (enabled/disabled)
   */
  public PushNotificationSettingResource(final String cameraId, final String cameraName, final boolean enabled) {
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
