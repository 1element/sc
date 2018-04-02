package com.github._1element.sc.domain; //NOSONAR

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entity for push notification settings.
 */
@Entity
public class PushNotificationSetting {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private String cameraId;

  private boolean enabled = false;

  protected PushNotificationSetting() {
  }

  public PushNotificationSetting(final String cameraId, final boolean enabled) {
    this.cameraId = cameraId;
    this.enabled = enabled;
  }

  public String getCameraId() {
    return cameraId;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

}
