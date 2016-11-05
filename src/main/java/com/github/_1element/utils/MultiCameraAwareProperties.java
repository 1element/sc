package com.github._1element.utils;

import com.github._1element.exception.PropertyNotFoundException;
import com.google.common.collect.ImmutableMap;
import com.ibm.icu.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Properties handling for multi camera.
 */
@Component
public class MultiCameraAwareProperties {

  @Autowired
  private Environment environment;

  private static final String PROPERTY_CAMERA_ID_PLACEHOLDER = "cameraId";

  public static final String PROPERTY_MULTI_CAMERA_PREFIX = "sc.camera[{" + PROPERTY_CAMERA_ID_PLACEHOLDER + "}].";

  private static final Logger log = LoggerFactory.getLogger(MultiCameraAwareProperties.class);

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

    String formattedPropertyKey = new MessageFormat(propertyKey).format(ImmutableMap.of(PROPERTY_CAMERA_ID_PLACEHOLDER, cameraId));

    if (!environment.containsProperty(formattedPropertyKey)) {
      throw new PropertyNotFoundException("Property not found for key '" + formattedPropertyKey + "'.");
    }

    return environment.getProperty(formattedPropertyKey);
  }

}
