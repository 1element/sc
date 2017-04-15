package com.github._1element.sc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.PushNotificationSetting;
import com.github._1element.sc.dto.CameraPushNotificationSettingResult;
import com.github._1element.sc.events.PushNotificationEvent;
import com.github._1element.sc.properties.PushNotificationProperties;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.PushNotificationSettingRepository;

import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverMessage;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SurveillanceCenterApplication.class})
@WebAppConfiguration
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest(PushNotificationService.class)
public class PushNotificationServiceTest {
  
  @Autowired
  PushNotificationSettingRepository pushNotificationSettingRepository;
  
  @Autowired
  CameraRepository cameraRepository;
  
  @Autowired
  PushNotificationProperties properties;
  
  @Mock
  private PushoverClient pushoverClient;

  @Autowired
  @InjectMocks
  PushNotificationService pushNotificationService;

  private static boolean setUpFinished = false;
 
  @Before
  public void setUp() throws Exception {
    if (setUpFinished) {
      return;
    }
    
    // test fixtures
    PushNotificationSetting pushNotificationSetting = new PushNotificationSetting("testcamera1", true);
    pushNotificationSettingRepository.save(pushNotificationSetting);
    
    // mocks
    MockitoAnnotations.initMocks(this);
    
    setUpFinished = true;
  }
  
  @Test
  public void testGetAllSettings() throws Exception {
    List<CameraPushNotificationSettingResult> settings = pushNotificationService.getAllSettings();
    
    assertTrue(settings.size() == 2);
    
    assertNotNull(settings.get(0));
    assertEquals("Backyard", settings.get(0).getCamera().getName());
    assertEquals("testcamera2", settings.get(0).getCamera().getId());
    assertFalse(settings.get(0).getPushNotificationSetting().isEnabled());
    
    assertNotNull(settings.get(1));
    assertEquals("Front door", settings.get(1).getCamera().getName());
    assertEquals("testcamera1", settings.get(1).getCamera().getId());
    assertTrue(settings.get(1).getPushNotificationSetting().isEnabled());
  }
  
  @Test
  public void testSendMessage() throws Exception {
    ReflectionTestUtils.setField(properties, "enabled", true);

    PushoverMessage.Builder pushoverMessageBuilder = PushoverMessage.builderWithApiToken(null)
        .setUserId(null)
        .setTitle("Test title")
        .setMessage("Test message")
        .setUrl("http://test.local/");
    PushoverMessage expectedMessage = pushoverMessageBuilder.build();

    pushNotificationService.sendMessage("Test title", "Test message", "http://test.local/");
    
    verify(pushoverClient).pushMessage(refEq(expectedMessage));
  }
  
  @Test
  public void testSendMessageDisabled() throws Exception {
    ReflectionTestUtils.setField(properties, "enabled", false);
    
    pushNotificationService.sendMessage("Disabled", "I should not be sent.");
    
    verifyZeroInteractions(pushoverClient);
  }
  
  @Test
  public void testHandlePushNotificationEvent() throws Exception {
    ReflectionTestUtils.setField(properties, "enabled", true);
    
    Camera camera = cameraRepository.findById("testcamera1");
    PushNotificationEvent pushNotificationEvent = new PushNotificationEvent(camera);
    
    pushNotificationService.handlePushNotificationEvent(pushNotificationEvent);
    
    verify(pushoverClient).pushMessage(any(PushoverMessage.class));
  }
  
  @Test
  public void testHandlePushNotificatonEventDisabledForCamera() throws Exception {
    ReflectionTestUtils.setField(properties, "enabled", true);
    
    Camera camera = cameraRepository.findById("testcamera2");
    PushNotificationEvent pushNotificationEvent = new PushNotificationEvent(camera);
    
    pushNotificationService.handlePushNotificationEvent(pushNotificationEvent);
    
    verifyZeroInteractions(pushoverClient);
  }

}
