package com.github._1element.sc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Push notification properties.
 */
@Component
@ConfigurationProperties("sc.pushnotification")
public class PushNotificationProperties {

  private boolean enabled = false;

  private String apiToken;

  private String userToken;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getApiToken() {
    return apiToken;
  }

  public void setApiToken(String apiToken) {
    this.apiToken = apiToken;
  }

  public String getUserToken() {
    return userToken;
  }

  public void setUserToken(String userToken) {
    this.userToken = userToken;
  }

}
