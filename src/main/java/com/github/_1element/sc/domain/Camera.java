package com.github._1element.sc.domain; //NOSONAR

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Surveillance camera POJO.
 */
public class Camera {

  private String id;

  private String name;

  @JsonIgnore
  private String host;

  @JsonIgnore
  private String ftpUsername;

  @JsonIgnore
  private String ftpPassword;

  @JsonIgnore
  private String ftpIncomingDirectory;

  private String snapshotUrl;

  private boolean snapshotEnabled;

  private boolean streamEnabled;

  /**
   * Constructs a new camera.
   *
   * @param id the unique id of the camera
   * @param name the camera name
   * @param host the (internal) host the camera is running on
   * @param ftpUsername the ftp username for incoming files
   * @param ftpPassword the ftp password for incoming files
   * @param ftpIncomingDirectory the ftp incoming directory
   * @param snapshotUrl optional url to retrieve snapshots
   * @param snapshotEnabled true if snapshots are enabled
   * @param streamEnabled true if streaming is enabled
   */
  public Camera(String id, String name, String host, String ftpUsername, String ftpPassword,
      String ftpIncomingDirectory, String snapshotUrl, boolean snapshotEnabled, boolean streamEnabled) {

    this.id = id;
    this.name = name;
    this.host = host;
    this.ftpUsername = ftpUsername;
    this.ftpPassword = ftpPassword;
    this.ftpIncomingDirectory = ftpIncomingDirectory;
    this.snapshotUrl = snapshotUrl;
    this.snapshotEnabled = snapshotEnabled;
    this.streamEnabled = streamEnabled;
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

  public String getFtpUsername() {
    return ftpUsername;
  }

  public String getFtpPassword() {
    return ftpPassword;
  }

  public String getFtpIncomingDirectory() {
    return ftpIncomingDirectory;
  }

  public String getSnapshotUrl() {
    return snapshotUrl;
  }

  public boolean isSnapshotEnabled() {
    return snapshotEnabled;
  }

  public boolean isStreamEnabled() {
    return streamEnabled;
  }

}
