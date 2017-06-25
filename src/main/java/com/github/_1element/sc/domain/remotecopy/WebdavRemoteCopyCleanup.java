package com.github._1element.sc.domain.remotecopy; //NOSONAR

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github._1element.sc.properties.WebdavRemoteCopyProperties;
import com.github._1element.sc.service.FileService;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;

/**
 * Cleanup old surveillance images on remote webdav server.
 */
@ConditionalOnProperty(name="sc.remotecopy.webdav.cleanup-enabled", havingValue="true")
@Component
public class WebdavRemoteCopyCleanup extends AbstractWebdavRemoteCopy implements RemoteCopyCleanup {

  private FileService fileService;
  
  private long sizeRemoved = 0;

  private long filesRemoved = 0;

  private long totalSize = 0;
  
  private static final String CRON_EVERY_DAY_AT_5_AM = "0 0 5 * * *";
  
  private static final Logger LOG = LoggerFactory.getLogger(WebdavRemoteCopyCleanup.class);

  @Autowired
  public WebdavRemoteCopyCleanup(Sardine sardine, WebdavRemoteCopyProperties webdavRemoteCopyProperties, FileService fileService) {
    super(sardine, webdavRemoteCopyProperties, fileService);
    this.fileService = fileService;
  }

  @Override
  @Scheduled(cron=CRON_EVERY_DAY_AT_5_AM)
  public void cleanup() {
    if (!webdavRemoteCopyProperties.isCleanupEnabled()) {
      LOG.info("Webdav remote copy cleanup task is disabled in configuration.");
      return;
    }

    sizeRemoved = 0;
    filesRemoved = 0;
    totalSize = 0;

    try {
      Map<Date, DavResource> remoteFileMap = removeFilesByDate();
      removeFilesByQuota(remoteFileMap);
    } catch (IOException e) {
      LOG.warn("Error during cleanup remote webdav images: {}", e.getMessage());
    }

    LOG.info("Cleanup job deleted {} files with {} disk space.", filesRemoved, FileUtils.byteCountToDisplaySize(sizeRemoved));
  }
  
  /**
   * Remove files from webdav server that are older than the configured number of days.
   * Returns map of still existing files on webdav after deletion.
   * 
   * @return map of files left
   * @throws IOException
   */
  private Map<Date, DavResource> removeFilesByDate() throws IOException {
    Map<Date, DavResource> remoteFileMap = new TreeMap<>();

    String baseLocation = webdavRemoteCopyProperties.getHost() + webdavRemoteCopyProperties.getDir();
    List<DavResource> baseResources = sardine.list(baseLocation);
    for (DavResource baseResource : baseResources) {
      boolean isParentResource = baseResource.getName().equals(webdavRemoteCopyProperties.getDir().replaceAll(SEPARATOR, ""));
      if (baseResource.isDirectory() && !isParentResource) {
        String location = baseLocation + baseResource.getName() + SEPARATOR;
        List<DavResource> resources = sardine.list(location);
        for (DavResource resource : resources) {
          if (fileService.hasValidExtension(resource.getName()) && (!removeResourceIfOutDated(resource))) {
            // put to return map for possible deletion by quota
            remoteFileMap.put(resource.getCreation(), resource);
            totalSize += resource.getContentLength();
          }
        }
      }
    }

    return remoteFileMap;
  }

  /**
   * Delete webdav resource if it's older than the configured days.
   * 
   * @param resource the webdav resource to delete
   * 
   * @return true if file has been deleted
   * @throws IOException
   */
  private boolean removeResourceIfOutDated(DavResource resource) throws IOException {
    LocalDateTime removeBefore = LocalDateTime.now().minusDays(webdavRemoteCopyProperties.getCleanupKeep());
    LocalDateTime remoteFileTimestamp = resource.getCreation().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    if (remoteFileTimestamp.isBefore(removeBefore)) {
      sardine.delete(webdavRemoteCopyProperties.getHost() + resource.getHref().toString());
      LOG.debug("Successfully removed file '{}' on remote webdav server, was older than {} days.", resource.getName(), webdavRemoteCopyProperties.getCleanupKeep());
      sizeRemoved += resource.getContentLength();
      filesRemoved++;

      return true;
    }

    return false;
  }

  /**
   * Removes files from webdav server if configured quota has been reached.
   * 
   * @param remoteFileMap map of webdav files to consider for deletion
   * @throws IOException
   */
  private void removeFilesByQuota(Map<Date, DavResource> remoteFileMap) throws IOException {
    if (totalSize < webdavRemoteCopyProperties.getCleanupMaxDiskSpace()) {
      // do nothing if max disk space/quota has not been reached
      return;
    }

    long sizeToBeRemoved = totalSize - webdavRemoteCopyProperties.getCleanupMaxDiskSpace();

    for (Map.Entry<Date, DavResource> entry: remoteFileMap.entrySet()) {
      DavResource davResource = entry.getValue();
      sardine.delete(webdavRemoteCopyProperties.getHost() + davResource.getHref().toString());
      LOG.debug("Successfully removed file '{}' on remote webdav server, quota was reached.", davResource.getName());
      sizeRemoved += davResource.getContentLength();
      filesRemoved++;
      if (sizeRemoved >= sizeToBeRemoved) {
        break;
      }
    }
  }

}
