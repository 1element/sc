package com.github._1element.sc.domain.remotecopy; //NOSONAR

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

import com.github._1element.sc.exception.FTPRemoteCopyException;
import com.github._1element.sc.properties.FTPRemoteCopyProperties;
import com.github._1element.sc.service.FileService;

/**
 * Cleanup old surveillance images on remote FTP server.
 */
@ConditionalOnProperty(name = "sc.remotecopy.ftp.cleanup-enabled", havingValue = "true")
@Component
public class FTPRemoteCopyCleanup extends AbstractFTPRemoteCopy implements RemoteCopyCleanup {

  private long sizeRemoved = 0;

  private long filesRemoved = 0;

  private long totalSize = 0;

  private static final String CRON_EVERY_DAY_AT_5_AM = "0 0 5 * * *";

  private static final Logger LOG = LoggerFactory.getLogger(FTPRemoteCopyCleanup.class);

  @Autowired
  public FTPRemoteCopyCleanup(FTPRemoteCopyProperties ftpRemoteCopyProperties, FTPClient ftp, FileService fileService) {
    super(ftpRemoteCopyProperties, ftp, fileService);
  }

  @Override
  @Scheduled(cron = CRON_EVERY_DAY_AT_5_AM)
  public void cleanup() {
    if (!ftpRemoteCopyProperties.isCleanupEnabled()) {
      LOG.info("FTP remote copy cleanup task is disabled in configuration.");
      return;
    }

    try {
      connect();
      removeOldFiles();
    } catch (Exception exception) {
      LOG.warn("Error during cleanup remote FTP images: {}", exception.getMessage());
    } finally {
      disconnect();
    }
  }

  /**
   * Delete old files from FTP server.
   * Files are deleted either if timestamp is too old or if quota is reached.
   *
   * @throws FTPRemoteCopyException exception if remote copy failed
   * @throws IOException exception in case of an IO error
   */
  private void removeOldFiles() throws FTPRemoteCopyException, IOException {
    if (!ftp.changeWorkingDirectory(ftpRemoteCopyProperties.getDir())) {
      throw new FTPRemoteCopyException("Could not change to directory '" + ftpRemoteCopyProperties.getDir()
          + "' on remote FTP server. Response was: " + ftp.getReplyString());
    }

    sizeRemoved = 0;
    filesRemoved = 0;
    totalSize = 0;

    Map<Instant, FTPFile> ftpFileMap = removeFilesByDate();
    removeFilesByQuota(ftpFileMap);

    LOG.info("Cleanup job deleted {} files with {} disk space.", filesRemoved,
        FileUtils.byteCountToDisplaySize(sizeRemoved));
  }

  /**
   * Remove files from FTP server that are older than the configured number of days.
   * Returns map of still existing files on FTP after deletion.
   *
   * @return map of all FTP files left
   * @throws IOException IO exception
   */
  private Map<Instant, FTPFile> removeFilesByDate() throws IOException {
    Map<Instant, FTPFile> ftpFileMap = new TreeMap<>();

    FTPFile[] ftpFiles = ftp.listFiles();
    for (FTPFile ftpFile : ftpFiles) {
      if (ftpFile.isFile() && fileService.hasValidExtension(ftpFile.getName())) {
        LocalDateTime removeBefore = LocalDateTime.now().minusDays(ftpRemoteCopyProperties.getCleanupKeep());
        LocalDateTime ftpFileTimestamp = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(ftpFile.getTimestamp().getTimeInMillis()), ZoneId.systemDefault());
        if (ftpFileTimestamp.isBefore(removeBefore) && ftp.deleteFile(ftpFile.getName())) {
          // delete straight if file is too old
          LOG.debug("Successfully removed file '{}' on remote FTP server, was older than {} days.",
              ftpFile.getName(), ftpRemoteCopyProperties.getCleanupKeep());
          sizeRemoved += ftpFile.getSize();
          filesRemoved++;
        } else {
          // put to return map for deletion by quota
          ftpFileMap.put(ftpFile.getTimestamp().toInstant(), ftpFile);
          totalSize += ftpFile.getSize();
        }
      }
    }

    return ftpFileMap;
  }

  /**
   * Removes files from FTP server if configured quota has been reached.
   *
   * @param ftpFileMap map of FTP files to consider for deletion
   * @throws IOException IO exception
   */
  private void removeFilesByQuota(Map<Instant, FTPFile> ftpFileMap) throws IOException {
    if (totalSize < ftpRemoteCopyProperties.getCleanupMaxDiskSpace()) {
      // do nothing if max disk space/quota has not been reached
      return;
    }

    long sizeToBeRemoved = totalSize - ftpRemoteCopyProperties.getCleanupMaxDiskSpace();

    for (Map.Entry<Instant, FTPFile> entry: ftpFileMap.entrySet()) {
      FTPFile ftpFile = entry.getValue();
      if (ftp.deleteFile(ftpFile.getName())) {
        LOG.debug("Successfully removed file '{}' on remote FTP server, quota was reached.", ftpFile.getName());
        sizeRemoved += ftpFile.getSize();
        filesRemoved++;
      }
      if (sizeRemoved >= sizeToBeRemoved) {
        break;
      }
    }
  }

}
