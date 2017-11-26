package com.github._1element.sc.repository; //NOSONAR

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.exception.PropertyNotFoundException;
import com.github._1element.sc.properties.MultiCameraAwareProperties;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.math.NumberUtils;
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

  private MultiCameraAwareProperties multiCameraAwareProperties;

  @Value("${sc.cameras.available}")
  private String camerasAvailable;

  private Map<String, Camera> cameras = new LinkedHashMap<>();

  private static final String PROPERTY_NAME = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "name";

  private static final String PROPERTY_ROTATION = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "rotation";

  private static final String PROPERTY_HOST = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "host";

  private static final String PROPERTY_FTP_USERNAME = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "ftp.username";

  private static final String PROPERTY_FTP_PASSWORD = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "ftp.password";

  private static final String PROPERTY_FTP_INCOMING_DIR = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "ftp.incoming-dir";

  private static final String PROPERTY_SNAPSHOT_URL = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "url.snapshot";

  private static final String PROPERTY_STREAM_URL = MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX + "url.stream";

  private static final String SEPARATOR = ",";

  @Autowired
  public CameraRepository(MultiCameraAwareProperties multiCameraAwareProperties) {
    this.multiCameraAwareProperties = multiCameraAwareProperties;
  }

  /**
   * Initialize repository. Read properties configuration.
   *
   * @throws PropertyNotFoundException
   */
  @PostConstruct
  private void initialize() throws PropertyNotFoundException {
    List<String> camerasAvailableList = Lists.newArrayList(Splitter.on(SEPARATOR).trimResults().omitEmptyStrings().split(camerasAvailable));

    for (String cameraId : camerasAvailableList) {
      String name = multiCameraAwareProperties.getProperty(PROPERTY_NAME, cameraId);
      Integer rotation = NumberUtils.createInteger(multiCameraAwareProperties.getProperty(PROPERTY_ROTATION, cameraId, null));
      String host = multiCameraAwareProperties.getProperty(PROPERTY_HOST, cameraId);
      String ftpUsername = multiCameraAwareProperties.getProperty(PROPERTY_FTP_USERNAME, cameraId);
      String ftpPassword = multiCameraAwareProperties.getProperty(PROPERTY_FTP_PASSWORD, cameraId);
      String ftpIncomingDirectory = multiCameraAwareProperties.getProperty(PROPERTY_FTP_INCOMING_DIR, cameraId);
      String snapshotUrl = multiCameraAwareProperties.getProperty(PROPERTY_SNAPSHOT_URL, cameraId, null);
      String streamUrl = multiCameraAwareProperties.getProperty(PROPERTY_STREAM_URL, cameraId, null);

      Camera camera = new Camera(cameraId, name, rotation, host, ftpUsername, ftpPassword, ftpIncomingDirectory, snapshotUrl, streamUrl);
      cameras.put(cameraId, camera);
    }
  }

  /**
   * Find camera by given id.
   *
   * @param cameraId camera identifier
   * @return
   */
  public Camera findById(String cameraId) {
    return cameras.get(cameraId);
  }

  /**
   * Returns all available cameras.
   *
   * @return
   */
  public List<Camera> findAll() {
    return cameras.values().stream().collect(Collectors.toList());
  }

  /**
   * Find all cameras which have a stream url configured.
   *
   * @return list of cameras with stream url
   */
  public List<Camera> findAllWithStreamUrl() {
    return cameras.values().stream().filter(Camera::hasStreamUrl).collect(Collectors.toList());
  }

  /**
   * Find all cameras which have a snapshot url configured.
   *
   * @return list of cameras with snapshot url
   */
  public List<Camera> findAllWithSnapshotUrl() {
    return cameras.values().stream().filter(Camera::hasSnapshotUrl).collect(Collectors.toList());
  }

  /**
   * Find camera by given ftp username.
   *
   * @param ftpUsername ftp username
   * @return
   */
  public Camera findByFtpUsername(String ftpUsername) {
    return cameras.values().stream()
      .filter(camera -> Objects.nonNull(camera.getFtpUsername()))
      .filter(camera -> camera.getFtpUsername().equals(ftpUsername))
      .findFirst().orElse(null);
  }

}
