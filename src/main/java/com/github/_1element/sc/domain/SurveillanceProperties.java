package com.github._1element.sc.domain;

import com.github._1element.sc.configuration.StaticResourceConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Surveillance properties component class.
 * This will expose some programmatic properties.
 */
@Component
public class SurveillanceProperties {

  private static final String IMAGE_THUMBNAIL_PREFIX = "thumbnail.";

  public String getImageThumbnailPrefix() {
    return IMAGE_THUMBNAIL_PREFIX;
  }

  public String getImageBaseUrl() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().path(StaticResourceConfiguration.IMAGES_PATH)
      .build().toString();
  }

}
