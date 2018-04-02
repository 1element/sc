package com.github._1element.sc.repository; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.CameraFtp;
import com.github._1element.sc.domain.CameraPicture;
import com.github._1element.sc.exception.PropertyNotFoundException;
import com.github._1element.sc.properties.MultiCameraAwareProperties;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Camera repository.
 */
@Component
public class CameraRepository {

  private final MultiCameraAwareProperties multiCameraAwareProperties;

  @Value("${sc.cameras.available}")
  private String camerasAvailable;

  private final Map<String, Camera> cameras = new LinkedHashMap<>();

  private static final String PROPERTY_NAME = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "name";

  private static final String PROPERTY_HOST = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "host";

  private static final String PROPERTY_FTP_USERNAME =
      MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "ftp.username";

  private static final String PROPERTY_FTP_PASSWORD =
      MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "ftp.password";

  private static final String PROPERTY_FTP_INCOMING_DIR =
      MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "ftp.incoming-dir";

  private static final String PROPERTY_MQTT_TOPIC =
      MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "mqtt.topic";

  private static final String PROPERTY_SNAPSHOT_URL =
      MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "snapshot-url";

  private static final String PROPERTY_SNAPSHOT_ENABLED =
      MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "snapshot-enabled";

  private static final String PROPERTY_STREAM_ENABLED =
      MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "stream-enabled";

  private static final String SEPARATOR = ",";

  @Autowired
  public CameraRepository(final MultiCameraAwareProperties multiCameraAwareProperties) {
    this.multiCameraAwareProperties = multiCameraAwareProperties;
  }

  /**
   * Initialize repository. Read properties configuration.
   *
   * @throws PropertyNotFoundException exception if property was not found
   */
  @PostConstruct
  private void initialize() throws PropertyNotFoundException {
    final List<String> camerasAvailableList = Lists.newArrayList(Splitter.on(SEPARATOR).trimResults().omitEmptyStrings()
        .split(camerasAvailable));

    for (final String cameraId : camerasAvailableList) {
      final String name = multiCameraAwareProperties.getProperty(PROPERTY_NAME, cameraId);
      final String host = multiCameraAwareProperties.getProperty(PROPERTY_HOST, cameraId);
      final String ftpUsername = multiCameraAwareProperties.getProperty(PROPERTY_FTP_USERNAME, cameraId);
      final String ftpPassword = multiCameraAwareProperties.getProperty(PROPERTY_FTP_PASSWORD, cameraId);
      final String ftpIncomingDirectory = multiCameraAwareProperties.getProperty(PROPERTY_FTP_INCOMING_DIR, cameraId);
      final String mqttTopic = multiCameraAwareProperties.getProperty(PROPERTY_MQTT_TOPIC, cameraId,
          String.class, null);
      final String snapshotUrl = multiCameraAwareProperties.getProperty(PROPERTY_SNAPSHOT_URL, cameraId,
          String.class, null);
      final boolean snapshotEnabled = multiCameraAwareProperties.getProperty(PROPERTY_SNAPSHOT_ENABLED, cameraId,
          boolean.class, true);
      final boolean streamEnabled = multiCameraAwareProperties.getProperty(PROPERTY_STREAM_ENABLED, cameraId,
          boolean.class, true);

      final CameraFtp ftp = new CameraFtp(ftpUsername, ftpPassword, ftpIncomingDirectory);
      final CameraPicture picture = new CameraPicture(snapshotUrl, snapshotEnabled, streamEnabled);

      final Camera camera = new Camera(cameraId, name, host, mqttTopic, ftp, picture);
      cameras.put(cameraId, camera);
    }
  }

  /**
   * Find camera by given id.
   *
   * @param cameraId camera identifier
   * @return the camera
   */
  public Camera findById(final String cameraId) {
    return cameras.get(cameraId);
  }

  /**
   * Returns all available cameras.
   *
   * @return the list of all available cameras
   */
  public List<Camera> findAll() {
    return cameras.values().stream().collect(Collectors.toList());
  }

  /**
   * Find camera by given ftp username.
   *
   * @param ftpUsername ftp username
   * @return the camera
   */
  public Camera findByFtpUsername(final String ftpUsername) {
    return cameras.values().stream()
      .filter(camera -> Objects.nonNull(camera.getFtp().getUsername()))
      .filter(camera -> camera.getFtp().getUsername().equals(ftpUsername))
      .findFirst().orElse(null);
  }

  /**
   * Find camera by given mqtt topic.
   *
   * @param mqttTopic the mqtt topic
   * @return the camera
   */
  public Camera findByMqttTopic(final String mqttTopic) {
    return cameras.values().stream()
      .filter(camera -> Objects.nonNull(camera.getMqttTopic()))
      .filter(camera -> camera.getMqttTopic().equals(mqttTopic))
      .findFirst().orElse(null);
  }

}
