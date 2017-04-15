package com.github._1element.sc.domain; //NOSONAR

import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.exception.FtpRemoteCopyException;
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
import org.springframework.context.annotation.Scope;
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
@Scope("prototype")
public class FtpRemoteCopy implements RemoteCopy {

  private FileService fileService;

  private FtpRemoteCopyProperties ftpRemoteCopyProperties;

  private FTPClient ftp;

  private long sizeRemoved = 0;

  private long filesRemoved = 0;

  private long totalSize = 0;

  private static final String CRON_EVERY_DAY_AT_5_AM = "0 0 5 * * *";

  private static final Logger LOG = LoggerFactory.getLogger(FtpRemoteCopy.class);

  @Autowired
  public FtpRemoteCopy(FtpRemoteCopyProperties ftpRemoteCopyProperties, FTPClient ftp, FileService fileService) {
    this.ftpRemoteCopyProperties = ftpRemoteCopyProperties;
    this.ftp = ftp;
    this.fileService = fileService;
  }

  @Override
  public void handle(RemoteCopyEvent remoteCopyEvent) {
    LOG.debug("Ftp remote copy handler for '{}' invoked.", remoteCopyEvent.getFileName());

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
      LOG.info("Ftp remote copy cleanup task is disabled in configuration.");
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
   * Connect to ftp server.
   *
   * @throws FtpRemoteCopyException
   * @throws IOException
   */
  private void connect() throws FtpRemoteCopyException, IOException {
    ftp.connect(ftpRemoteCopyProperties.getHost());

    if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
      throw new FtpRemoteCopyException("Could not connect to remote ftp server '" + ftpRemoteCopyProperties.getHost() + "'. Response was: " + ftp.getReplyString());
    }

    if (!ftp.login(ftpRemoteCopyProperties.getUsername(), ftpRemoteCopyProperties.getPassword())) {
      throw new FtpRemoteCopyException("Could not login to remote ftp server. Invalid username or password.");
    }
    ftp.setFileType(FTP.BINARY_FILE_TYPE);
    ftp.enterLocalPassiveMode();
  }

  /**
   * Delete old files from ftp server.
   * Files are deleted either if timestamp is too old or if quota is reached.
   *
   * @throws FtpRemoteCopyException
   * @throws IOException
   */
  private void removeOldFiles() throws FtpRemoteCopyException, IOException {
    if (!ftp.changeWorkingDirectory(ftpRemoteCopyProperties.getDir())) {
      throw new FtpRemoteCopyException("Could not change to directory '" + ftpRemoteCopyProperties.getDir() + "' on remote ftp server. Response was: " + ftp.getReplyString());
    }

    sizeRemoved = 0;
    filesRemoved = 0;
    totalSize = 0;

    Map<Calendar, FTPFile> ftpFileMap = removeFilesByDate();
    removeFilesByQuota(ftpFileMap);

    LOG.info("Cleanup job deleted " + filesRemoved + " files with " + FileUtils.byteCountToDisplaySize(sizeRemoved) + " disk space.");
  }

  /**
   * Remove files from ftp server that are older than the configured number of days.
   * Returns map of still existing files on ftp after deletion.
   *
   * @return map of all ftp files left
   * @throws IOException
   */
  private Map<Calendar, FTPFile> removeFilesByDate() throws IOException {
    Map<Calendar, FTPFile> ftpFileMap = new TreeMap<>();

    FTPFile[] ftpFiles = ftp.listFiles();
    for (FTPFile ftpFile : ftpFiles) {
      if (ftpFile.isFile() && fileService.hasValidExtension(ftpFile.getName())) {
        LocalDateTime removeBefore = LocalDateTime.now().minusDays(ftpRemoteCopyProperties.getCleanupKeep());
        LocalDateTime ftpFileTimestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(ftpFile.getTimestamp().getTimeInMillis()), ZoneId.systemDefault());
        if (ftpFileTimestamp.isBefore(removeBefore) && ftp.deleteFile(ftpFile.getName())) {
          // delete straight if file is too old
          LOG.debug("Successfully removed file '{}' on remote ftp server, was older than {} days.", ftpFile.getName(), ftpRemoteCopyProperties.getCleanupKeep());
          sizeRemoved += ftpFile.getSize();
          filesRemoved++;
        } else {
          // put to return map for deletion by quota
          ftpFileMap.put(ftpFile.getTimestamp(), ftpFile);
          totalSize += ftpFile.getSize();
        }
      }
    }

    return ftpFileMap;
  }

  /**
   * Removes files from ftp server if configured quota has been reached.
   *
   * @param ftpFileMap map of ftp files to consider for deletion
   * @throws IOException
   */
  private void removeFilesByQuota(Map<Calendar, FTPFile> ftpFileMap) throws IOException {
    if (totalSize < ftpRemoteCopyProperties.getCleanupMaxDiskSpace()) {
      // do nothing if max disk space/quota has not been reached
      return;
    }

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

  /**
   * Transfer file to ftp server.
   *
   * @param localFullFilepath full path to local file
   * @throws FtpRemoteCopyException
   * @throws IOException
   */
  private void transferFile(String localFullFilepath) throws FtpRemoteCopyException, IOException {
    File file = new File(localFullFilepath);
    InputStream inputStream = new FileInputStream(file);

    if (!ftp.storeFile(ftpRemoteCopyProperties.getDir() + file.getName(), inputStream)) {
      throw new FtpRemoteCopyException("Could not upload file to remote ftp server. Response was: " + ftp.getReplyString());
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
