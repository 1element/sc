package com.github._1element.sc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for new recordings notifications.
 */
@Component
@ConfigurationProperties("sc.notifier")
public class NotifierProperties {

  private boolean enabled = true;

  private int interval = 180;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getInterval() {
    return interval;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

}
