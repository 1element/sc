package com.github._1element.sc.domain; //NOSONAR

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getRotation() {
    return rotation;
  }

  public void setRotation(Integer rotation) {
    this.rotation = rotation;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getFtpUsername() {
    return ftpUsername;
  }

  public void setFtpUsername(String ftpUsername) {
    this.ftpUsername = ftpUsername;
  }

  public String getFtpPassword() {
    return ftpPassword;
  }

  public void setFtpPassword(String ftpPassword) {
    this.ftpPassword = ftpPassword;
  }

  public String getFtpIncomingDirectory() {
    return ftpIncomingDirectory;
  }

  public void setFtpIncomingDirectory(String ftpIncomingDirectory) {
    this.ftpIncomingDirectory = ftpIncomingDirectory;
  }

  public String getSnapshotUrl() {
    return snapshotUrl;
  }

  public void setSnapshotUrl(String snapshotUrl) {
    this.snapshotUrl = snapshotUrl;
  }

  public String getStreamUrl() {
    return streamUrl;
  }

  public void setStreamUrl(String streamUrl) {
    this.streamUrl = streamUrl;
  }

}
