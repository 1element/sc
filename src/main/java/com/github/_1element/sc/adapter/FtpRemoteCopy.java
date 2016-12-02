package com.github._1element.sc.adapter;

import com.github._1element.sc.events.RemoteCopyEvent;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copy surveillance image to ftp remote server (backup).
 */
@ConditionalOnProperty(name="sc.remotecopy.ftp.enabled", havingValue="true")
@Component
public class FtpRemoteCopy implements RemoteCopy {

  @Value("${sc.remotecopy.ftp.host:null}")
  private String host;

  @Value("${sc.remotecopy.ftp.username:null}")
  private String username;

  @Value("${sc.remotecopy.ftp.password:null}")
  private String password;

  @Value("${sc.remotecopy.ftp.dir:/}")
  private String directory;

  private FTPClient ftp;

  private static final Logger LOG = LoggerFactory.getLogger(FtpRemoteCopy.class);

  @Override
  public void handle(RemoteCopyEvent remoteCopyEvent) {
    remoteCopyEvent.getFileName();

    try {
      connect();
      transferFile(remoteCopyEvent.getFileName());
    } catch (Exception e) {
      LOG.warn("Error during remote ftp copy: {}", e.getMessage());
    } finally {
      disconnect();
    }
  }

  /**
   * Connect to ftp server.
   *
   * @throws Exception
   */
  private void connect() throws Exception {
    ftp = new FTPClient();
    ftp.connect(host);

    if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
      throw new Exception("Could not connect to remote ftp server '" + host + "'. Response was: " + ftp.getReplyString());
    }

    if (!ftp.login(username, password)) {
      throw new Exception("Could not login to remote ftp server. Invalid username or password.");
    }
    ftp.setFileType(FTP.BINARY_FILE_TYPE);
    ftp.enterLocalPassiveMode();
  }

  /**
   * Transfer file to ftp server.
   *
   * @param localFullFilepath full path to local file
   * @throws Exception
   */
  private void transferFile(String localFullFilepath) throws Exception {
    File file = new File(localFullFilepath);
    InputStream inputStream = new FileInputStream(file);

    if (!ftp.storeFile(directory + file.getName(), inputStream)) {
      throw new Exception("Could not upload file to remote ftp server. Response was: " + ftp.getReplyString());
    }

    LOG.info("File '{}' was successfully uploaded to remote ftp server.", file.getName());
  }

  /**
   * Disconnect from ftp server.
   */
  private void disconnect() {
    if (ftp != null && ftp.isConnected()) {
      try {
        ftp.logout();
        ftp.disconnect();
      } catch (IOException e) {
        // silently ignore disconnect exceptions
      }
    }
  }

}
