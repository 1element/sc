package com.github._1element.sc.properties;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.exception.PropertyNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class MultiCameraAwarePropertiesTest {

  @Autowired
  private MultiCameraAwareProperties multiCameraAwareProperties;

  @Test
  public void testGetProperty() throws Exception {
    assertEquals("Front door", multiCameraAwareProperties.getProperty(
        MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "name", "testcamera1"));
  }

  @Test(expected = PropertyNotFoundException.class)
  public void testGetPropertyNotFound() throws Exception {
    multiCameraAwareProperties.getProperty(
        MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "invalidKey", "invalidCameraId");
  }

  @Test
  public void testGetPropertyBoolean() throws Exception {
    assertFalse(multiCameraAwareProperties.getProperty(MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX
        + "stream-enabled", "testcamera2", boolean.class));

    assertTrue(multiCameraAwareProperties.getProperty(MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX
        + "snapshot-enabled", "testcamera2", boolean.class));
  }

  @Test
  public void testGetPropertyWithDefault() throws Exception {
    assertEquals("Backyard", multiCameraAwareProperties.getProperty(
        MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "name", "testcamera2", String.class,
        "DefaultWillNotBeUsed"));

    assertEquals("PassedDefault", multiCameraAwareProperties.getProperty(
        MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "invalidKey", "testcamera2", String.class,
        "PassedDefault"));

    assertEquals("PassedDefaultCameraValue", multiCameraAwareProperties.getProperty(
        MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "name", "invalidCamera", String.class,
        "PassedDefaultCameraValue"));
  }

}
