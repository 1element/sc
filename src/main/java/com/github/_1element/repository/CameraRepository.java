package com.github._1element.repository;

import com.github._1element.domain.Camera;
import com.github._1element.exception.PropertyNotFoundException;
import com.github._1element.utils.MultiCameraAwareProperties;
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

import static com.github._1element.utils.MultiCameraAwareProperties.PROPERTY_MULTI_CAMERA_PREFIX;

/**
 * Camera repository.
 */
@Component
public class CameraRepository {

  @Autowired
  private MultiCameraAwareProperties multiCameraAwareProperties;

  @Value("${sc.cameras.available}")
  private String camerasAvailable;

  private Map<String, Camera> cameras = new HashMap<String, Camera>();

  private static final String PROPERTY_NAME = PROPERTY_MULTI_CAMERA_PREFIX + "name";

  private static final String PROPERTY_ROTATION = PROPERTY_MULTI_CAMERA_PREFIX + "rotation";

  private static final String PROPERTY_FTP_USERNAME = PROPERTY_MULTI_CAMERA_PREFIX + "ftp.username";

  private static final String PROPERTY_FTP_PASSWORD = PROPERTY_MULTI_CAMERA_PREFIX + "ftp.password";

  private static final String PROPERTY_FTP_INCOMING_DIR = PROPERTY_MULTI_CAMERA_PREFIX + "ftp.incoming-dir";

  private static final String PROPERTY_SNAPSHOT_URL = PROPERTY_MULTI_CAMERA_PREFIX + "url.snapshot";

  private static final String SEPARATOR = ",";

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
      camera.setFtpUsername(multiCameraAwareProperties.getProperty(PROPERTY_FTP_USERNAME, cameraId));
      camera.setFtpPassword(multiCameraAwareProperties.getProperty(PROPERTY_FTP_PASSWORD, cameraId));
      camera.setFtpIncomingDirectory(multiCameraAwareProperties.getProperty(PROPERTY_FTP_INCOMING_DIR, cameraId));
      camera.setSnapshotUrl(multiCameraAwareProperties.getProperty(PROPERTY_SNAPSHOT_URL, cameraId));
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
    return new ArrayList<Camera>(cameras.values());
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
