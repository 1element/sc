package com.github._1element.sc.domain.remotecopy; //NOSONAR

import org.springframework.beans.factory.annotation.Autowired;

import com.github._1element.sc.properties.WebdavRemoteCopyProperties;
import com.github._1element.sc.service.FileService;
import com.github.sardine.Sardine;

/**
 * Abstract webdav remote copy class.
 */
public abstract class AbstractWebdavRemoteCopy {

  protected static final String SEPARATOR = "/";

  protected Sardine sardine;

  protected WebdavRemoteCopyProperties webdavRemoteCopyProperties;

  protected FileService fileService;

  /**
   * Constructor.
   *
   * @param sardine the sardine webdav dependency
   * @param webdavRemoteCopyProperties the properties to use for remote copying
   * @param fileService the file service dependency
   */
  @Autowired
  public AbstractWebdavRemoteCopy(Sardine sardine, WebdavRemoteCopyProperties webdavRemoteCopyProperties,
                                  FileService fileService) {
    this.sardine = sardine;
    this.webdavRemoteCopyProperties = webdavRemoteCopyProperties;
    this.fileService = fileService;
    setCredentials();
  }

  protected void setCredentials() {
    sardine.setCredentials(webdavRemoteCopyProperties.getUsername(), webdavRemoteCopyProperties.getPassword());
    sardine.enablePreemptiveAuthentication(webdavRemoteCopyProperties.getHost());
  }

}
