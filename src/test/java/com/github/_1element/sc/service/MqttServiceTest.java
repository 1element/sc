package com.github._1element.sc.service;

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.events.ImageReceivedEvent;
import com.github._1element.sc.properties.MqttProperties;
import com.github._1element.sc.repository.CameraRepository;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MqttServiceTest {

  @Mock
  private CameraRepository cameraRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private MqttProperties properties;

  @Mock
  private MqttClient mqttClient;

  @InjectMocks
  private MqttService mqttService;

  @Before
  public void setUp() throws Exception {
    mqttService = new MqttService(cameraRepository, eventPublisher, properties);
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testMessageArrived() throws Exception {
    // arrange
    byte[] payload = "payload".getBytes();
    MqttMessage mqttMessage = mock(MqttMessage.class);
    when(mqttMessage.getPayload()).thenReturn(payload);
    Camera camera = mock(Camera.class);
    when(cameraRepository.findByMqttTopic("mqttTopic")).thenReturn(camera);
    ImageReceivedEvent expectedImageReceivedEvent = new ImageReceivedEvent(payload, camera);

    // act
    mqttService.messageArrived("mqttTopic", mqttMessage);

    // assert
    verify(eventPublisher).publishEvent(refEq(expectedImageReceivedEvent));
  }

  @Test
  public void testMessageArrivedNoTopic() throws Exception {
    // arrange
    MqttMessage mqttMessage = mock(MqttMessage.class);
    when(cameraRepository.findByMqttTopic("unknownMqttTopic")).thenReturn(null);

    // act
    mqttService.messageArrived("unknownMqttTopic", mqttMessage);

    // assert
    verifyZeroInteractions(eventPublisher);
  }

  @Test
  public void testConnectComplete() throws Exception {
    // act
    mqttService.connectComplete(false, "brokerUri");

    // assert
    verifyZeroInteractions(mqttClient);
  }

  @Test
  public void testConnectCompleteReconnect() throws Exception {
    // arrange
    when(properties.getTopicFilter()).thenReturn("topic-filter");

    // act
    mqttService.connectComplete(true, "brokerUri");

    // assert
    verify(mqttClient).subscribe("topic-filter");
  }

}
