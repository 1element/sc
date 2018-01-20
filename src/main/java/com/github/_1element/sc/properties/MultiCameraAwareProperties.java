package com.github._1element.sc.properties; //NOSONAR

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
   * Returns property value for the provided key and camera id.
   *
   * @param propertyKey the property key to resolve
   * @param cameraId the camera id to use for the property key building
   * @param targetType the expected type of the property value
   *
   * @return property value
   * @throws PropertyNotFoundException exception if property was not found
   */
  public <T> T getProperty(String propertyKey, String cameraId, Class<T> targetType) throws PropertyNotFoundException {
    if (StringUtils.isBlank(cameraId)) {
      throw new PropertyNotFoundException(String.format("Property '%s' not found. Empty camera id was given.",
          propertyKey));
    }

    String formattedPropertyKey = String.format(propertyKey, cameraId);

    if (!environment.containsProperty(formattedPropertyKey)) {
      throw new PropertyNotFoundException(String.format("Property not found for key '%s'.", formattedPropertyKey));
    }

    return environment.getProperty(formattedPropertyKey, targetType);
  }

  /**
   * Returns property value for the provided key and camera id. Default value if none found.
   *
   * @param propertyKey the property key to resolve
   * @param cameraId the camera id to use for the property key building
   * @param targetType the expected type of the property value
   * @param defaultValue the default value if none found
   *
   * @return property value
   */
  public <T> T getProperty(String propertyKey, String cameraId, Class<T> targetType, T defaultValue) {
    try {
      return getProperty(propertyKey, cameraId, targetType);
    } catch (Exception exception) {
      return defaultValue;
    }
  }

  /**
   * Returns string property value for given key and camera id.
   *
   * @param propertyKey the property key to resolve
   * @param cameraId the camera id to use for the property key building
   *
   * @return property value
   * @throws PropertyNotFoundException exception if property was not found
   */
  public String getProperty(String propertyKey, String cameraId) throws PropertyNotFoundException {
    return getProperty(propertyKey, cameraId, String.class);
  }

}
