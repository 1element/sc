package com.github._1element.sc.domain; //NOSONAR

/**
 * Camera ftp value object.
 */
public class CameraFtp {

  private final String username;
  private final String password;
  private final String incomingDirectory;

  /**
   * Constructs a new camera ftp value object.
   *
   * @param username the ftp username for incoming files
   * @param password the ftp password for incoming files
   * @param incomingDirectory the ftp incoming directory
   */
  public CameraFtp(final String username, final String password, final String incomingDirectory) {
    this.username = username;
    this.password = password;
    this.incomingDirectory = incomingDirectory;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getIncomingDirectory() {
    return incomingDirectory;
  }

}
