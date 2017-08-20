package com.github._1element.sc.domain.remotecopy; //NOSONAR

import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.exception.FTPRemoteCopyException;
import com.github._1element.sc.properties.FTPRemoteCopyProperties;
import com.github._1element.sc.service.FileService;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Copy surveillance image to FTP remote server (backup).
 */
@ConditionalOnProperty(name="sc.remotecopy.ftp.enabled", havingValue="true")
@Component
@Scope("prototype")
public class FTPRemoteCopy extends AbstractFTPRemoteCopy implements RemoteCopy {

  private static final Logger LOG = LoggerFactory.getLogger(FTPRemoteCopy.class);

  @Autowired
  public FTPRemoteCopy(FTPRemoteCopyProperties ftpRemoteCopyProperties, FTPClient ftp, FileService fileService) {
    super(ftpRemoteCopyProperties, ftp, fileService);
  }

  @Override
  public void handle(RemoteCopyEvent remoteCopyEvent) {
    LOG.debug("FTP remote copy handler for '{}' invoked.", remoteCopyEvent.getFileName());

    try {
      connect();
      transferFile(remoteCopyEvent.getFileName());
    } catch (Exception e) {
      LOG.warn("Error during remote FTP copy: {}", e.getMessage());
    } finally {
      disconnect();
    }
  }

  /**
   * Transfer file to FTP server.
   *
   * @param localFullFilepath full path to local file
   * @throws FTPRemoteCopyException
   * @throws IOException
   */
  private void transferFile(String localFullFilepath) throws FTPRemoteCopyException, IOException {
    File file = fileService.createFile(localFullFilepath);

    try (InputStream inputStream = fileService.createInputStream(file)) {
      if (!ftp.storeFile(ftpRemoteCopyProperties.getDir() + file.getName(), inputStream)) {
        throw new FTPRemoteCopyException("Could not upload file to remote FTP server. Response was: " + ftp.getReplyString());
      }
    }

    LOG.info("File '{}' was successfully uploaded to remote FTP server.", file.getName());
  }

}
