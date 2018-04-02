package com.github._1element.sc.domain; //NOSONAR

/**
 * Surveillance camera POJO.
 */
public class Camera {

  private final String id;

  private final String name;

  private final String host;

  private final String mqttTopic;

  private final CameraFtp ftp;

  private final CameraPicture picture;

  /**
   * Constructs a new camera.
   *
   * @param id the unique id of the camera
   * @param name the camera name
   * @param host the (internal) host the camera is running on
   * @param mqttTopic the mqtt topic
   * @param ftp the ftp settings
   * @param picture the picture settings
   */
  public Camera(final String id, final String name, final String host,
                final String mqttTopic, final CameraFtp ftp, final CameraPicture picture) {
    this.id = id;
    this.name = name;
    this.host = host;
    this.ftp = ftp;
    this.picture = picture;
    this.mqttTopic = mqttTopic;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getHost() {
    return host;
  }

  public CameraFtp getFtp() {
    return ftp;
  }

  public CameraPicture getPicture() {
    return picture;
  }

  public String getMqttTopic() {
    return mqttTopic;
  }

}
