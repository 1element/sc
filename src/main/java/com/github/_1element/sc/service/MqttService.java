package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.events.ImageReceivedEvent;
import com.github._1element.sc.properties.MqttProperties;
import com.github._1element.sc.repository.CameraRepository;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * MQTT subscriber client for incoming surveillance images.
 */
@ConditionalOnProperty(name = "sc.mqtt.enabled", havingValue = "true")
@Component
public class MqttService implements MqttCallbackExtended {

  private CameraRepository cameraRepository;

  private ApplicationEventPublisher eventPublisher;

  private MqttProperties properties;

  private MqttClient mqttClient;

  private static final Logger LOG = LoggerFactory.getLogger(MqttService.class);

  /**
   * Constructor.
   *
   * @param cameraRepository the camera repository
   * @param eventPublisher the event publisher
   * @param properties the mqtt properties
   */
  @Autowired
  public MqttService(CameraRepository cameraRepository, ApplicationEventPublisher eventPublisher,
                     MqttProperties properties) {
    this.cameraRepository = cameraRepository;
    this.eventPublisher = eventPublisher;
    this.properties = properties;
  }

  /**
   * Construct and start MQTT client.
   */
  @PostConstruct
  public void start() {
    Objects.requireNonNull(properties.getBrokerConnection(),
        "MQTT broker connection URL must not be null. Check your configuration.");

    try {
      mqttClient = new MqttClient(properties.getBrokerConnection(), MqttClient.generateClientId());
      mqttClient.setCallback(this);
      mqttClient.connect(createConnectOptions());
      subscribe();
    } catch (MqttException exception) {
      LOG.error("Error during connection to MQTT broker '{}', reason: '{}'",
          properties.getBrokerConnection(), exception.getMessage());
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    LOG.debug("MQTT message arrived for topic '{}'", topic);

    Camera camera = cameraRepository.findByMqttTopic(topic);
    if (camera == null) {
      LOG.warn("No matching camera found for MQTT topic '{}'. Incoming image was not processed.", topic);
      return;
    }

    eventPublisher.publishEvent(new ImageReceivedEvent(message.getPayload(), camera));
  }

  @Override
  public void connectComplete(boolean reconnect, String serverURI) {
    if (reconnect) {
      subscribe();
    }
    LOG.debug("Successfully connected to MQTT broker '{}', reconnect: {}", serverURI, reconnect);
  }

  @Override
  public void connectionLost(Throwable cause) {
    LOG.debug("Connection lost to MQTT broker, cause: '{}'", cause.getMessage());
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    // no-op
  }

  private MqttConnectOptions createConnectOptions() {
    MqttConnectOptions connectOptions = new MqttConnectOptions();
    connectOptions.setCleanSession(false);
    connectOptions.setAutomaticReconnect(true);
    connectOptions.setUserName(properties.getUsername());
    connectOptions.setPassword(properties.getPassword().toCharArray());

    return connectOptions;
  }

  private void subscribe() {
    try {
      mqttClient.subscribe(properties.getTopicFilter());
    } catch (MqttException exception) {
      LOG.warn("Error during subscribe for topic '{}', cause '{}'", properties.getTopicFilter(),
          exception.getMessage());
    }
  }

}
