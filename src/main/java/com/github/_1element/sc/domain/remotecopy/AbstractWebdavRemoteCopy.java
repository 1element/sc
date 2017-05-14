package com.github._1element.sc.domain.remotecopy; //NOSONAR

import org.springframework.beans.factory.annotation.Autowired;

import com.github._1element.sc.properties.WebdavRemoteCopyProperties;
import com.github.sardine.Sardine;

/**
 * Abstract webdav remote copy class.
 */
public abstract class AbstractWebdavRemoteCopy {

  protected Sardine sardine;

  protected WebdavRemoteCopyProperties webdavRemoteCopyProperties;

  @Autowired
  public AbstractWebdavRemoteCopy(Sardine sardine, WebdavRemoteCopyProperties webdavRemoteCopyProperties) {
    this.sardine = sardine;
    this.webdavRemoteCopyProperties = webdavRemoteCopyProperties;
    setCredentials();
  }

  protected void setCredentials() {
    sardine.setCredentials(webdavRemoteCopyProperties.getUsername(), webdavRemoteCopyProperties.getPassword());
  }
  
}
