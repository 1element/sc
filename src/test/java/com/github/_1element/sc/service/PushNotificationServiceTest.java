package com.github._1element.sc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.github._1element.sc.dto.PushNotificationSettingResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.PushNotificationSetting;
import com.github._1element.sc.domain.pushnotification.PushNotificationClient;
import com.github._1element.sc.domain.pushnotification.PushNotificationClientFactory;
import com.github._1element.sc.events.PushNotificationEvent;
import com.github._1element.sc.properties.PushNotificationProperties;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.PushNotificationSettingRepository;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class PushNotificationServiceTest {

  @Autowired
  private PushNotificationSettingRepository pushNotificationSettingRepository;

  @Autowired
  private CameraRepository cameraRepository;

  @Autowired
  private PushNotificationProperties properties;

  @Autowired
  private PushNotificationService pushNotificationService;

  @Mock
  private PushNotificationClientFactory pushNotificationClientFactoryMock;

  private PushNotificationClient pushNotificationClientMock;

  private static boolean fixturesCreated = false;

  /**
   * Setup for all tests.
   *
   * @throws Exception exception in case of an error
   */
  @Before
  public void setUp() throws Exception {
    // mocks
    MockitoAnnotations.initMocks(this);
    pushNotificationClientMock = mock(PushNotificationClient.class);
    Mockito.when(pushNotificationClientFactoryMock.getClient(any())).thenReturn(pushNotificationClientMock);
    ReflectionTestUtils.setField(pushNotificationService, "pushNotificationClientFactory",
        pushNotificationClientFactoryMock);

    ReflectionTestUtils.setField(properties, "enabled", true);
    ReflectionTestUtils.setField(properties, "groupTime", 0);

    if (!fixturesCreated) {
      // test fixtures
      PushNotificationSetting pushNotificationSetting = new PushNotificationSetting("testcamera1", true);
      pushNotificationSettingRepository.save(pushNotificationSetting);

      fixturesCreated = true;
    }
  }

  @Test
  public void testGetAllSettings() throws Exception {
    List<PushNotificationSettingResource> settings = pushNotificationService.getAllSettings();

    assertTrue(settings.size() == 4);

    assertNotNull(settings.get(0));
    assertEquals("Front door", settings.get(0).getCameraName());
    assertEquals("testcamera1", settings.get(0).getCameraId());
    assertTrue(settings.get(0).isEnabled());

    assertNotNull(settings.get(1));
    assertEquals("Backyard", settings.get(1).getCameraName());
    assertEquals("testcamera2", settings.get(1).getCameraId());
    assertFalse(settings.get(1).isEnabled());
  }

  @Test
  public void testSendMessage() throws Exception {
    pushNotificationService.sendMessage("Test title", "Test message");

    verify(pushNotificationClientMock).sendMessage("Test title", "Test message");
  }

  @Test
  public void testSendMessageDisabled() throws Exception {
    ReflectionTestUtils.setField(properties, "enabled", false);

    pushNotificationService.sendMessage("Disabled", "I should not be sent.");

    verifyZeroInteractions(pushNotificationClientFactoryMock);
  }

  @Test
  public void testHandlePushNotificationEvent() throws Exception {
    Camera camera = cameraRepository.findById("testcamera1");
    PushNotificationEvent pushNotificationEvent = new PushNotificationEvent(camera);

    pushNotificationService.handlePushNotificationEvent(pushNotificationEvent);

    verify(pushNotificationClientMock).sendMessage(any(), any());
  }

  @Test
  public void testHandlePushNotificationEventDisabledForCamera() throws Exception {
    Camera camera = cameraRepository.findById("testcamera2");
    PushNotificationEvent pushNotificationEvent = new PushNotificationEvent(camera);

    pushNotificationService.handlePushNotificationEvent(pushNotificationEvent);

    verifyZeroInteractions(pushNotificationClientFactoryMock);
  }

  @Test
  public void testHandlePushNotificationEventGroupTime() throws Exception {
    ReflectionTestUtils.setField(properties, "groupTime", 2);

    Camera camera = cameraRepository.findById("testcamera1");
    PushNotificationEvent pushNotificationEvent = new PushNotificationEvent(camera);

    // send two events shortly
    pushNotificationService.handlePushNotificationEvent(pushNotificationEvent);
    pushNotificationService.handlePushNotificationEvent(pushNotificationEvent);

    // sendMessage() should only be invoked once (group time should be applied)
    verify(pushNotificationClientMock, times(1)).sendMessage(any(), any());
  }

  @Test
  public void testHasRateLimitReached() throws Exception {
    PushNotificationProperties propertiesPojo = new PushNotificationProperties();
    propertiesPojo.setGroupTime(3);

    PushNotificationService pushNotificationServicePojo =
        new PushNotificationService(null, null, null, propertiesPojo, null);

    assertFalse(pushNotificationServicePojo.hasRateLimitReached("testcamera3"));
    assertTrue(pushNotificationServicePojo.hasRateLimitReached("testcamera3"));
    assertFalse(pushNotificationServicePojo.hasRateLimitReached("testcamera4"));
  }

  @Test
  public void testHasRateLimitReachedWithNoGroupTime() throws Exception {
    PushNotificationProperties propertiesPojo = new PushNotificationProperties();
    PushNotificationService pushNotificationServicePojo =
        new PushNotificationService(null, null, null, propertiesPojo, null);

    assertFalse(pushNotificationServicePojo.hasRateLimitReached("testcamera3"));
    assertFalse(pushNotificationServicePojo.hasRateLimitReached("testcamera3"));
  }

}
