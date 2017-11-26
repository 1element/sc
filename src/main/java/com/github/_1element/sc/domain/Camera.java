package com.github._1element.sc.domain; //NOSONAR

/**
 * Surveillance camera POJO.
 */
public class Camera {

  private String id;

  private String name;

  private Integer rotation;

  private String host;

  private String ftpUsername;

  private String ftpPassword;

  private String ftpIncomingDirectory;

  private String snapshotUrl;

  private String streamUrl;

  public Camera(String id, String name, Integer rotation, String host, String ftpUsername, String ftpPassword,
      String ftpIncomingDirectory, String snapshotUrl, String streamUrl) {
    this.id = id;
    this.name = name;
    this.rotation = rotation;
    this.host = host;
    this.ftpUsername = ftpUsername;
    this.ftpPassword = ftpPassword;
    this.ftpIncomingDirectory = ftpIncomingDirectory;
    this.snapshotUrl = snapshotUrl;
    this.streamUrl = streamUrl;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Integer getRotation() {
    return rotation;
  }

  public String getHost() {
    return host;
  }

  public String getFtpUsername() {
    return ftpUsername;
  }

  public String getFtpPassword() {
    return ftpPassword;
  }

  public String getFtpIncomingDirectory() {
    return ftpIncomingDirectory;
  }

  public boolean hasSnapshotUrl() {
    return snapshotUrl != null;
  }

  public String getSnapshotUrl() {
    return snapshotUrl;
  }

  public boolean hasStreamUrl() {
    return streamUrl != null;
  }

  public String getStreamUrl() {
    return streamUrl;
  }

}
