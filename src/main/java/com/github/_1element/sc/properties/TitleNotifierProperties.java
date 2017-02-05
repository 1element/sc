package com.github._1element.sc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for title notifications.
 */
@Component
@ConfigurationProperties("sc.title-notifier")
public class TitleNotifierProperties {

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
