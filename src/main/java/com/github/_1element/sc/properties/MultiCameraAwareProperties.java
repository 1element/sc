package com.github._1element.sc.properties;

import com.github._1element.sc.exception.PropertyNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Properties handling for multi camera.
 */
@Component
public class MultiCameraAwareProperties {

  private Environment environment;

  public static final String PROPERTY_MULTI_CAMERA_PREFIX = "sc.camera[%s].";

  @Autowired
  public MultiCameraAwareProperties(Environment environment) {
    this.environment = environment;
  }

  /**
   * Returns property value for given key and camera id. Default value if nothing was found.
   *
   * @param propertyKey  property key
   * @param cameraId     camera identifier
   * @param defaultValue default value
   * @return
   */
  public String getProperty(String propertyKey, String cameraId, String defaultValue) {
    try {
      return getProperty(propertyKey, cameraId);
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * Returns property value for given key and camera id.
   *
   * @param propertyKey property key
   * @param cameraId    camera identifier
   * @return
   * @throws PropertyNotFoundException
   */
  public String getProperty(String propertyKey, String cameraId) throws PropertyNotFoundException {
    if (StringUtils.isBlank(cameraId)) {
      throw new PropertyNotFoundException("Property '" + propertyKey + "' not found. Empty camera id was given.");
    }

    String formattedPropertyKey = String.format(propertyKey, cameraId);

    if (!environment.containsProperty(formattedPropertyKey)) {
      throw new PropertyNotFoundException("Property not found for key '" + formattedPropertyKey + "'.");
    }

    return environment.getProperty(formattedPropertyKey);
  }

}
