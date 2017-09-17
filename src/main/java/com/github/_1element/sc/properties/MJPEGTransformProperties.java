package com.github._1element.sc.properties; //NOSONAR

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties for MJPEG transformation.
 */
@Component
@ConfigurationProperties("sc.transform.mjpeg")
public class MJPEGTransformProperties {

  private boolean enabled = false;

  private int delay = 500;

  private String[] urls;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    this.delay = delay;
  }

  public String[] getUrls() {
    return urls;
  }

  public void setUrls(String[] urls) {
    this.urls = urls;
  }

}
