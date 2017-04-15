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

  private long groupTime = 0;
  
  private String url;

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

  public long getGroupTime() {
    return groupTime;
  }

  public void setGroupTime(long groupTime) {
    this.groupTime = groupTime;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
  
}
