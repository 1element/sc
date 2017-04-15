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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Camera repository.
 */
@Component
public class CameraRepository {

  private MultiCameraAwareProperties multiCameraAwareProperties;

  @Value("${sc.cameras.available}")
  private String camerasAvailable;

  private Map<String, Camera> cameras = new HashMap<>();

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
      Camera camera = new Camera();
      camera.setId(cameraId);
      camera.setName(multiCameraAwareProperties.getProperty(PROPERTY_NAME, cameraId));
      camera.setRotation(NumberUtils.createInteger(multiCameraAwareProperties.getProperty(PROPERTY_ROTATION, cameraId, null)));
      camera.setHost(multiCameraAwareProperties.getProperty(PROPERTY_HOST, cameraId));
      camera.setFtpUsername(multiCameraAwareProperties.getProperty(PROPERTY_FTP_USERNAME, cameraId));
      camera.setFtpPassword(multiCameraAwareProperties.getProperty(PROPERTY_FTP_PASSWORD, cameraId));
      camera.setFtpIncomingDirectory(multiCameraAwareProperties.getProperty(PROPERTY_FTP_INCOMING_DIR, cameraId));
      camera.setSnapshotUrl(multiCameraAwareProperties.getProperty(PROPERTY_SNAPSHOT_URL, cameraId));
      camera.setStreamUrl(multiCameraAwareProperties.getProperty(PROPERTY_STREAM_URL, cameraId));
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
    if (cameraId == null) {
      return null;
    }

    for (Camera camera : findAll()) {
      if (cameraId.equals(camera.getId())) {
        return camera;
      }
    }

    return null;
  }

  /**
   * Returns all available cameras.
   *
   * @return
   */
  public List<Camera> findAll() {
    return new ArrayList<>(cameras.values());
  }

  /**
   * Find camera by given ftp username.
   *
   * @param ftpUsername ftp username
   * @return
   */
  public Camera findByFtpUsername(String ftpUsername) {
    if (ftpUsername == null) {
      return null;
    }

    for (Camera camera : findAll()) {
      if (ftpUsername.equals(camera.getFtpUsername())) {
        return camera;
      }
    }

    return null;
  }

}
