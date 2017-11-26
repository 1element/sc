package com.github._1element.sc.domain.remotecopy; //NOSONAR

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

  private long sizeRemoved = 0;

  private long filesRemoved = 0;

  private long totalSize = 0;
  
  private static final String CRON_EVERY_DAY_AT_6_AM = "0 0 6 * * *";
  
  private static final Logger LOG = LoggerFactory.getLogger(WebdavRemoteCopyCleanup.class);

  @Autowired
  public WebdavRemoteCopyCleanup(Sardine sardine, WebdavRemoteCopyProperties webdavRemoteCopyProperties, FileService fileService) {
    super(sardine, webdavRemoteCopyProperties, fileService);
    this.fileService = fileService;
  }

  @Override
  @Scheduled(cron=CRON_EVERY_DAY_AT_6_AM)
  public void cleanup() {
    if (!webdavRemoteCopyProperties.isCleanupEnabled()) {
      LOG.info("Webdav remote copy cleanup task is disabled in configuration.");
      return;
    }

    sizeRemoved = 0;
    filesRemoved = 0;
    totalSize = 0;

    try {
      Map<Instant, DavResource> remoteFileMap = removeFilesByDate();
      removeFilesByQuota(remoteFileMap);
    } catch (IOException exception) {
      LOG.warn("Major error during cleanup remote webdav images: {}", exception.getMessage());
    }

    LOG.info("Cleanup job deleted {} files with {} disk space.", filesRemoved,
      FileUtils.byteCountToDisplaySize(sizeRemoved));
  }

  /**
   * Remove files from webdav server that are older than the configured number of days.
   * Returns map of still existing files on webdav after deletion.
   *
   * @return map of files left
   * @throws IOException IO Exception in case of failure
   */
  private Map<Instant, DavResource> removeFilesByDate() throws IOException {
    Map<Instant, DavResource> remoteFileMap = new TreeMap<>();

    String baseLocation = webdavRemoteCopyProperties.getHost() + webdavRemoteCopyProperties.getDir();
    List<DavResource> baseResources = sardine.list(baseLocation);
    for (DavResource baseResource : baseResources) {
      if (baseResource.isDirectory() && !isParentResource(baseResource)) {
        String location = baseLocation + baseResource.getName() + SEPARATOR;
        Map<Instant, DavResource> remoteFileMapForSubdirectory = removeFilesByDateForLocation(location);
        remoteFileMap.putAll(remoteFileMapForSubdirectory);
      }
    }

    return remoteFileMap;
  }

  /**
   * Remove files for given location by age.
   *
   * @param location the webdav location to operate on
   * @return map of files left after deletion
   */
  private Map<Instant, DavResource> removeFilesByDateForLocation(String location) {
    Map<Instant, DavResource> remoteFileMap = new TreeMap<>();

    try {
      List<DavResource> resources = sardine.list(location);
      if (removeDirectoryIfEmpty(location, resources)) {
        return remoteFileMap;
      }

      for (DavResource resource : resources) {
        if (fileService.hasValidExtension(resource.getName()) && (!removeResourceIfOutDated(resource))) {
          // put to return map for possible deletion by quota
          remoteFileMap.put(resource.getCreation().toInstant(), resource);
          totalSize += resource.getContentLength();
        }
      }
    } catch (IOException exception) {
      LOG.warn("Could not read remote webdav directory '{}', error message: '{}'", location, exception.getMessage());
    }

    return remoteFileMap;
  }

  /**
   * Delete webdav resource if it's older than the configured days.
   *
   * @param resource the webdav resource to delete
   *
   * @return true if file has been deleted
   */
  private boolean removeResourceIfOutDated(DavResource resource) {
    LocalDateTime removeBefore = LocalDateTime.now().minusDays(webdavRemoteCopyProperties.getCleanupKeep());
    LocalDateTime remoteFileTimestamp = resource.getCreation().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

    if (remoteFileTimestamp.isBefore(removeBefore)) {
      String location = webdavRemoteCopyProperties.getHost() + resource.getHref().toString();
      try {
        sardine.delete(location);
        LOG.debug("Successfully removed file '{}' on remote webdav server, was older than {} days.", resource.getName(), webdavRemoteCopyProperties.getCleanupKeep());
        sizeRemoved += resource.getContentLength();
        filesRemoved++;
      } catch (IOException e) {
        LOG.warn("Error while deleting '{}' on remote webdav server: '{}'", location, e.getMessage());
      }

      return true;
    }

    return false;
  }

  /**
   * Remove directory if it's empty.
   * This is the case if it only contains one element (the parent directory itself).
   * 
   * @param location the directory location
   * @param resourcesList the list of resources for that location
   * 
   * @return true if directory is empty and has been deleted
   */
  private boolean removeDirectoryIfEmpty(String location, List<DavResource> resourcesList) {
    if (resourcesList != null && resourcesList.size() == 1) {
      try {
        sardine.delete(location);
      } catch (IOException e) {
        LOG.warn("Error while deleting directory '{}' on remote webdav server: '{}'", location, e.getMessage());
      }

      return true;
    }

    return false;
  }

  /**
   * Removes files from webdav server if configured quota has been reached.
   *
   * @param remoteFileMap map of webdav files to consider for deletion
   */
  private void removeFilesByQuota(Map<Instant, DavResource> remoteFileMap) {
    if (totalSize < webdavRemoteCopyProperties.getCleanupMaxDiskSpace()) {
      // do nothing if max disk space/quota has not been reached
      return;
    }

    long sizeToBeRemoved = totalSize - webdavRemoteCopyProperties.getCleanupMaxDiskSpace();

    for (Map.Entry<Instant, DavResource> entry: remoteFileMap.entrySet()) {
      DavResource davResource = entry.getValue();
      String location = webdavRemoteCopyProperties.getHost() + davResource.getHref().toString();
      try {
        sardine.delete(location);
        LOG.debug("Successfully removed file '{}' on remote webdav server, quota was reached.", davResource.getName());
        sizeRemoved += davResource.getContentLength();
        filesRemoved++;
      } catch (IOException e) {
        LOG.warn("Error while deleting '{}' on remote webdav server: '{}'", location, e.getMessage());
      }
      if (sizeRemoved >= sizeToBeRemoved) {
        break;
      }
    }
  }

  /**
   * Returns true if given resource is the parent/base resource.
   * 
   * @param resource the resource to check
   * @return true if parent/base resource
   */
  private boolean isParentResource(DavResource resource) {
    return resource.getName().equals(webdavRemoteCopyProperties.getDir().replaceAll(SEPARATOR, ""));
  }

}
