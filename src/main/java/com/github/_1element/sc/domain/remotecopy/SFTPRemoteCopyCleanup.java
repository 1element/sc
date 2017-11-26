package com.github._1element.sc.domain.remotecopy; //NOSONAR

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github._1element.sc.exception.SFTPRemoteCopyException;
import com.github._1element.sc.properties.SFTPRemoteCopyProperties;
import com.github._1element.sc.service.FileService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpException;

/**
 * Cleanup old surveillance images on remote SFTP server.
 */
@ConditionalOnProperty(name = "sc.remotecopy.sftp.cleanup-enabled", havingValue = "true")
@Component
public class SFTPRemoteCopyCleanup extends AbstractSFTPRemoteCopy implements RemoteCopyCleanup {

  private long sizeRemoved = 0;

  private long filesRemoved = 0;

  private long totalSize = 0;

  private static final String CRON_EVERY_DAY_AT_4_AM = "0 0 4 * * *";

  private static final Logger LOG = LoggerFactory.getLogger(SFTPRemoteCopyCleanup.class);

  @Autowired
  public SFTPRemoteCopyCleanup(SFTPRemoteCopyProperties sftpRemoteCopyProperties, JSch jsch, FileService fileService) {
    super(sftpRemoteCopyProperties, jsch, fileService);
  }

  @Override
  @Scheduled(cron = CRON_EVERY_DAY_AT_4_AM)
  public void cleanup() {
    if (!sftpRemoteCopyProperties.isCleanupEnabled()) {
      LOG.info("SFTP remote copy cleanup task is disabled in configuration.");
      return;
    }

    ChannelSftp sftpChannel = null;
    try {
      sftpChannel = createSFTPChannel();
      removeOldFiles(sftpChannel);
    } catch (SFTPRemoteCopyException exception) {
      LOG.warn("Error during cleanup remote SFTP images: {}", exception.getMessage());
    } finally {
      if (sftpChannel != null) {
        sftpChannel.disconnect();
      }
      disconnectSession();
    }
  }

  /**
   * Delete old files.
   * Files are deleted either if timestamp is too old or if quota is reached.
   *
   * @param sftpChannel the SFTP channel to use for retrieval and deletion
   * @throws SFTPRemoteCopyException exception if remote copy failed
   */
  private void removeOldFiles(ChannelSftp sftpChannel) throws SFTPRemoteCopyException {
    sizeRemoved = 0;
    filesRemoved = 0;
    totalSize = 0;

    Map<LocalDateTime, ChannelSftp.LsEntry> sftpFileMap = removeFilesByDate(sftpChannel);
    removeFilesByQuota(sftpChannel, sftpFileMap);

    LOG.info("Cleanup job deleted {} files with {} disk space.", filesRemoved,
        FileUtils.byteCountToDisplaySize(sizeRemoved));
  }

  /**
   * Remove files that are older than the configured number of days.
   * Returns map of still existing files on SFTP after deletion.
   *
   * @param sftpChannel the SFTP channel to use for retrieval and deletion
   * @return map of all SFTP files left
   * @throws SFTPRemoteCopyException exception if remote copy failed
   */
  @SuppressWarnings("unchecked")
  private Map<LocalDateTime, ChannelSftp.LsEntry> removeFilesByDate(ChannelSftp sftpChannel)
      throws SFTPRemoteCopyException {
    Map<LocalDateTime, ChannelSftp.LsEntry> resultFileMap = new TreeMap<>();
    LocalDateTime removeBefore = LocalDateTime.now().minusDays(sftpRemoteCopyProperties.getCleanupKeep());

    Vector<ChannelSftp.LsEntry> fileList;
    try {
      fileList = sftpChannel.ls(sftpRemoteCopyProperties.getDir());
    } catch (SftpException exception) {
      throw new SFTPRemoteCopyException("Could not get remote directory listing for: "
          + sftpRemoteCopyProperties.getDir(), exception);
    }

    for (ChannelSftp.LsEntry entry : fileList) {
      if (!entry.getAttrs().isDir() && fileService.hasValidExtension(entry.getFilename())) {
        LocalDateTime sftpFileTimestamp = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(entry.getAttrs().getMTime()), ZoneId.systemDefault());
        if (sftpFileTimestamp.isBefore(removeBefore)) {
          // delete straight if file is too old
          String filePath = sftpRemoteCopyProperties.getDir() + entry.getFilename();
          try {
            sftpChannel.rm(filePath);
            LOG.debug("Successfully removed file '{}' on remote SFTP server, was older than {} days.",
                filePath, sftpRemoteCopyProperties.getCleanupKeep());
            sizeRemoved += entry.getAttrs().getSize();
            filesRemoved++;
          } catch (SftpException exception) {
            LOG.warn("Could not delete file '{}' on remote SFTP server: '{}'", filePath, exception.getMessage());
          }
        } else {
          // put to result map for deletion by quota
          resultFileMap.put(sftpFileTimestamp, entry);
          totalSize += entry.getAttrs().getSize();
        }
      }
    }

    return resultFileMap;
  }

  /**
   * Removes files if configured quota has been reached.
   *
   * @param sftpChannel the SFTP channel to use for deletion
   * @param sftpFileMap map of SFTP files to consider for deletion
   */
  private void removeFilesByQuota(ChannelSftp sftpChannel, Map<LocalDateTime, ChannelSftp.LsEntry> sftpFileMap) {
    if (totalSize < sftpRemoteCopyProperties.getCleanupMaxDiskSpace()) {
      // do nothing if max disk space/quota has not been reached
      return;
    }

    long sizeToBeRemoved = totalSize - sftpRemoteCopyProperties.getCleanupMaxDiskSpace();

    for (Map.Entry<LocalDateTime, ChannelSftp.LsEntry> entry: sftpFileMap.entrySet()) {
      ChannelSftp.LsEntry sftpLsEntry = entry.getValue();
      String filePath = sftpRemoteCopyProperties.getDir() + sftpLsEntry.getFilename();
      try {
        sftpChannel.rm(filePath);
        LOG.debug("Successfully removed file '{}' on remote SFTP server, quota was reached.",
            sftpLsEntry.getFilename());
        sizeRemoved += sftpLsEntry.getAttrs().getSize();
        filesRemoved++;
      } catch (SftpException exception) {
        LOG.warn("Could not delete file '{}' on remote SFTP server: '{}'", filePath, exception.getMessage());
      }

      if (sizeRemoved >= sizeToBeRemoved) {
        break;
      }
    }
  }

}
