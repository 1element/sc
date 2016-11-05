package com.github._1element.domain;

public class Camera {

  private String id;

  private String name;

  private String ftpUsername;

  private String ftpPassword;

  private String ftpIncomingDirectory;

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

}
