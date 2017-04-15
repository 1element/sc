package com.github._1element.sc.domain;

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

  public PushNotificationSetting(String cameraId, boolean enabled) {
    this.cameraId = cameraId;
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

}
