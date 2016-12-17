package com.github._1element.sc.domain;

import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.properties.FtpRemoteCopyProperties;
import com.github._1element.sc.service.FileService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

/**
 * Copy surveillance image to ftp remote server (backup).
 */
@ConditionalOnProperty(name="sc.remotecopy.ftp.enabled", havingValue="true")
@Component
public class FtpRemoteCopy implements RemoteCopy {

  @Autowired
  private FileService fileService;

  @Autowired
  private FtpRemoteCopyProperties ftpRemoteCopyProperties;

  private FTPClient ftp;

  private static final String CRON_EVERY_DAY_AT_5_AM = "0 0 5 * * *";

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

  @Override
  @Scheduled(cron=CRON_EVERY_DAY_AT_5_AM)
  public void cleanup() {
    if (!ftpRemoteCopyProperties.isCleanupEnabled()) {
      return;
    }

    try {
      connect();
      removeOldFiles();
    } catch (Exception e) {
      LOG.warn("Error during cleanup remote ftp images: {}", e.getMessage());
    } finally {
      disconnect();
    }
  }

  /**
   * Returns instance of ftp client.
   * @return ftp client
   */
  private FTPClient getFTPClient() {
    if (ftp == null) {
      return new FTPClient();
    }

    return ftp;
  }

  /**
   * Connect to ftp server.
   *
   * @throws Exception
   */
  private void connect() throws Exception {
    ftp = getFTPClient();
    ftp.connect(ftpRemoteCopyProperties.getHost());

    if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
      throw new Exception("Could not connect to remote ftp server '" + ftpRemoteCopyProperties.getHost() + "'. Response was: " + ftp.getReplyString());
    }

    if (!ftp.login(ftpRemoteCopyProperties.getUsername(), ftpRemoteCopyProperties.getPassword())) {
      throw new Exception("Could not login to remote ftp server. Invalid username or password.");
    }
    ftp.setFileType(FTP.BINARY_FILE_TYPE);
    ftp.enterLocalPassiveMode();
  }

  /**
   * Delete old files from ftp server.
   * Files are deleted either if timestamp is too old or if quota is reached.
   *
   * @throws Exception
   */
  private void removeOldFiles() throws Exception {
    if (!ftp.changeWorkingDirectory(ftpRemoteCopyProperties.getDir())) {
      throw new Exception("Could not change to directory '" + ftpRemoteCopyProperties.getDir() + "' on remote ftp server. Response was: " + ftp.getReplyString());
    }

    Map<Calendar, FTPFile> ftpFileMap = new TreeMap<Calendar, FTPFile>();
    long totalSize = 0;
    long sizeRemoved = 0;
    long filesRemoved = 0;

    FTPFile[] ftpFiles = ftp.listFiles();
    for (FTPFile ftpFile : ftpFiles) {
      if (ftpFile.isFile() && fileService.hasValidExtension(ftpFile.getName())) {
        LocalDateTime removeBefore = LocalDateTime.now().minusDays(ftpRemoteCopyProperties.getCleanupKeep());
        LocalDateTime ftpFileTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(ftpFile.getTimestamp().getTimeInMillis()), ZoneId.systemDefault());
        if (ftpFileTimestamp.isBefore(removeBefore)) {
          // delete straight if file is too old
          if (ftp.deleteFile(ftpFile.getName())) {
            LOG.debug("Successfully removed file '{}' on remote ftp server, was older than {} days.", ftpFile.getName(), ftpRemoteCopyProperties.getCleanupKeep());
            sizeRemoved += ftpFile.getSize();
            filesRemoved++;
          }
        } else {
          // put to map for deletion by quota
          ftpFileMap.put(ftpFile.getTimestamp(), ftpFile);
          totalSize += ftpFile.getSize();
        }
      }
    }

    // check if max disk space/quota has been reached
    if (totalSize > ftpRemoteCopyProperties.getCleanupMaxDiskSpace()) {
      long sizeToBeRemoved = totalSize - ftpRemoteCopyProperties.getCleanupMaxDiskSpace();

      for (Map.Entry<Calendar, FTPFile> entry: ftpFileMap.entrySet()) {
        FTPFile ftpFile = entry.getValue();
        if (ftp.deleteFile(ftpFile.getName())) {
          LOG.debug("Successfully removed file '{}' on remote ftp server, quota was reached.", ftpFile.getName());
          sizeRemoved += ftpFile.getSize();
          filesRemoved++;
        }
        if (sizeRemoved >= sizeToBeRemoved) {
          break;
        }
      }
    }

    LOG.info("Cleanup job deleted " + filesRemoved + " files with " + FileUtils.byteCountToDisplaySize(sizeRemoved) + " disk space.");
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

    if (!ftp.storeFile(ftpRemoteCopyProperties.getDir() + file.getName(), inputStream)) {
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
