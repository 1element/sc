package com.github._1element.sc.domain; //NOSONAR

/**
 * Surveillance camera POJO.
 */
public class Camera {

  private String id;

  private String name;

  private String host;

  private String mqttTopic;

  private CameraFtp ftp;

  private CameraPicture picture;

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
  public Camera(String id, String name, String host,
                String mqttTopic, CameraFtp ftp, CameraPicture picture) {
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
