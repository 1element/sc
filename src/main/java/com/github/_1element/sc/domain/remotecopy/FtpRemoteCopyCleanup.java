package com.github._1element.sc.domain.remotecopy; //NOSONAR

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github._1element.sc.exception.FtpRemoteCopyException;
import com.github._1element.sc.properties.FtpRemoteCopyProperties;
import com.github._1element.sc.service.FileService;

/**
 * Cleanup old surveillance images on remote ftp server.
 */
@ConditionalOnProperty(name="sc.remotecopy.ftp.cleanup-enabled", havingValue="true")
@Component
public class FtpRemoteCopyCleanup extends AbstractFtpRemoteCopy implements RemoteCopyCleanup {

  private FileService fileService;

  private long sizeRemoved = 0;

  private long filesRemoved = 0;

  private long totalSize = 0;

  private static final String CRON_EVERY_DAY_AT_5_AM = "0 0 5 * * *";

  private static final Logger LOG = LoggerFactory.getLogger(FtpRemoteCopyCleanup.class);

  @Autowired
  public FtpRemoteCopyCleanup(FtpRemoteCopyProperties ftpRemoteCopyProperties, FTPClient ftp, FileService fileService) {
    super(ftpRemoteCopyProperties, ftp);
    this.fileService = fileService;
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

    LOG.info("Cleanup job deleted {} files with {} disk space.", filesRemoved, FileUtils.byteCountToDisplaySize(sizeRemoved));
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
  
}
