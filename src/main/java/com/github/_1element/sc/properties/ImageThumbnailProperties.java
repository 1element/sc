package com.github._1element.sc.properties; //NOSONAR

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Image thumbnail specific configuration properties.
 */
@Component
@ConfigurationProperties("sc.image.thumbnail")
public class ImageThumbnailProperties {

  private int width = 200;

  private int height = 200;

  private double quality = 0.8;

  public int getWidth() {
    return width;
  }

  public void setWidth(final int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(final int height) {
    this.height = height;
  }

  public double getQuality() {
    return quality;
  }

  public void setQuality(final double quality) {
    this.quality = quality;
  }

}
