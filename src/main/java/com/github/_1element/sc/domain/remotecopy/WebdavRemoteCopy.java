package com.github._1element.sc.domain.remotecopy; //NOSONAR

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.properties.WebdavRemoteCopyProperties;
import com.github.sardine.Sardine;

/**
 * Copy surveillance image to remote webdav server (backup).
 */
@ConditionalOnProperty(name="sc.remotecopy.webdav.enabled", havingValue="true")
@Component
@Scope("prototype")
public class WebdavRemoteCopy extends AbstractWebdavRemoteCopy implements RemoteCopy {
  
  private static final Logger LOG = LoggerFactory.getLogger(WebdavRemoteCopy.class);

  @Autowired
  public WebdavRemoteCopy(Sardine sardine, WebdavRemoteCopyProperties webdavRemoteCopyProperties) {
    super(sardine, webdavRemoteCopyProperties);
  }

  @Override
  public void handle(RemoteCopyEvent remoteCopyEvent) {
    LOG.debug("Webdav remote copy handler for '{}' invoked.", remoteCopyEvent.getFileName());

    try {
      transferFile(remoteCopyEvent.getFileName());
    } catch (IOException e) {
      LOG.warn("Error during copy to remote webdav server: {}", e.getMessage());
    }
  }

  /**
   * Transfer file to webdav server.
   *
   * @param localFullFilepath full path to local file
   */
  private void transferFile(String localFullFilepath) throws IOException {
    File file = new File(localFullFilepath);
    InputStream inputStream = new FileInputStream(file);

    String destination = webdavRemoteCopyProperties.getHost() + webdavRemoteCopyProperties.getDir() + file.getName();
    sardine.put(destination, inputStream);

    LOG.info("File '{}' was successfully uploaded to remote webdav server.", file.getName());
  }

}
