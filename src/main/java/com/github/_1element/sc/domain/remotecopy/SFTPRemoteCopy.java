package com.github._1element.sc.domain.remotecopy; //NOSONAR

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.exception.SFTPRemoteCopyException;
import com.github._1element.sc.properties.SFTPRemoteCopyProperties;
import com.github._1element.sc.service.FileService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpException;

/**
 * Copy surveillance image to SFTP remote server (backup).
 */
@ConditionalOnProperty(name="sc.remotecopy.sftp.enabled", havingValue="true")
@Component
@Scope("prototype")
public class SFTPRemoteCopy extends AbstractSFTPRemoteCopy implements RemoteCopy {

  private static final Logger LOG = LoggerFactory.getLogger(SFTPRemoteCopy.class);

  @Autowired
  public SFTPRemoteCopy(SFTPRemoteCopyProperties sFtpRemoteCopyProperties, JSch jsch, FileService fileService) {
    super(sFtpRemoteCopyProperties, jsch, fileService);
  }

  @Override
  public void handle(RemoteCopyEvent remoteCopyEvent) {
    LOG.debug("SFTP remote copy handler for '{}' invoked.", remoteCopyEvent.getFileName());

    ChannelSftp sftpChannel = null;
    try {
      sftpChannel = createSFTPChannel();
      transferFile(remoteCopyEvent.getFileName(), sftpChannel);
    } catch (SFTPRemoteCopyException e) {
      LOG.warn("Error during remote SFTP copy: '{}'", e.getMessage());
    } finally {
      if (sftpChannel != null) {
        sftpChannel.disconnect();
      }
      disconnectSession();
    }
  }

  /**
   * Uploads a file using the given SFTP channel.
   *
   * @param localFullFilepath the full path to the local file to upload
   * @param sftpChannel the SFTP channel that will be used for the transfer
   * @throws SFTPRemoteCopyException 
   */
  private void transferFile(String localFullFilepath, ChannelSftp sftpChannel) throws SFTPRemoteCopyException {
    File file = fileService.createFile(localFullFilepath);

    try (InputStream inputStream = fileService.createInputStream(file)) {
      sftpChannel.cd(sftpRemoteCopyProperties.getDir());
      sftpChannel.put(inputStream, file.getName());
    } catch (SftpException | IOException e) {
      throw new SFTPRemoteCopyException("Could not upload file to remote SFTP server: " + e.getMessage(), e);
    }

    LOG.info("File '{}' was successfully uploaded to remote SFTP server.", file.getName());
  }

}
