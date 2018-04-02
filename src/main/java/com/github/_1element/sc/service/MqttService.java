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

  private final CameraRepository cameraRepository;

  private final ApplicationEventPublisher eventPublisher;

  private final MqttProperties properties;

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
  public MqttService(final CameraRepository cameraRepository, final ApplicationEventPublisher eventPublisher,
                     final MqttProperties properties) {
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
    } catch (final MqttException exception) {
      LOG.error("Error during connection to MQTT broker '{}', reason: '{}'",
          properties.getBrokerConnection(), exception.getMessage());
    }
  }

  @Override
  public void messageArrived(final String topic, final MqttMessage message) throws Exception {
    LOG.debug("MQTT message arrived for topic '{}'", topic);

    final Camera camera = cameraRepository.findByMqttTopic(topic);
    if (camera == null) {
      LOG.warn("No matching camera found for MQTT topic '{}'. Incoming image was not processed.", topic);
      return;
    }

    eventPublisher.publishEvent(new ImageReceivedEvent(message.getPayload(), camera));
  }

  @Override
  public void connectComplete(final boolean reconnect, final String serverURI) {
    if (reconnect) {
      subscribe();
    }
    LOG.debug("Successfully connected to MQTT broker '{}', reconnect: {}", serverURI, reconnect);
  }

  @Override
  public void connectionLost(final Throwable cause) {
    LOG.debug("Connection lost to MQTT broker, cause: '{}'", cause.getMessage());
  }

  @Override
  public void deliveryComplete(final IMqttDeliveryToken token) {
    // no-op
  }

  /**
   * Create connect options.
   *
   * @return connect options
   */
  private MqttConnectOptions createConnectOptions() {
    final MqttConnectOptions connectOptions = new MqttConnectOptions();
    connectOptions.setCleanSession(false);
    connectOptions.setAutomaticReconnect(true);
    connectOptions.setUserName(properties.getUsername());
    connectOptions.setPassword(properties.getPassword().toCharArray());

    return connectOptions;
  }

  /**
   * Subscribe to topic.
   */
  private void subscribe() {
    try {
      mqttClient.subscribe(properties.getTopicFilter());
    } catch (final MqttException exception) {
      LOG.warn("Error during subscribe for topic '{}', cause '{}'", properties.getTopicFilter(),
          exception.getMessage());
    }
  }

}
