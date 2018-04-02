package com.github._1element.sc.properties; //NOSONAR

import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for FTP remote copy.
 */
@Component
@ConfigurationProperties("sc.remotecopy.ftp")
public class FTPRemoteCopyProperties {

  private boolean enabled;

  private String host;

  private String dir = "/";

  private String username;

  private String password;

  private boolean cleanupEnabled = false;

  private long cleanupMaxDiskSpace;

  private int cleanupKeep;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public String getHost() {
    return host;
  }

  public void setHost(final String host) {
    this.host = host;
  }

  public String getDir() {
    return dir;
  }

  public void setDir(final String dir) {
    this.dir = dir;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

  public boolean isCleanupEnabled() {
    return cleanupEnabled;
  }

  public void setCleanupEnabled(final boolean cleanupEnabled) {
    this.cleanupEnabled = cleanupEnabled;
  }

  public long getCleanupMaxDiskSpace() {
    return cleanupMaxDiskSpace;
  }

  public void setCleanupMaxDiskSpace(final long cleanupMaxDiskSpace) {
    this.cleanupMaxDiskSpace = FileUtils.ONE_MB * cleanupMaxDiskSpace;
  }

  public int getCleanupKeep() {
    return cleanupKeep;
  }

  public void setCleanupKeep(final int cleanupKeep) {
    this.cleanupKeep = cleanupKeep;
  }

}
