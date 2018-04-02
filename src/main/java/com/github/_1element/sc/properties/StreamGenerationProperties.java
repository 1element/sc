package com.github._1element.sc.properties; //NOSONAR

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sc.stream-generation")
public class StreamGenerationProperties {

  private int mjpegDelay = 500;

  public int getMjpegDelay() {
    return mjpegDelay;
  }

  public void setMjpegDelay(final int mjpegDelay) {
    this.mjpegDelay = mjpegDelay;
  }

}
