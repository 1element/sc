package com.github._1element.sc.properties; //NOSONAR

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Image specific configuration properties.
 */
@Component
@ConfigurationProperties("sc.image")
public class ImageProperties {

  private String storageDir;

  private String[] validExtensions;

  private int pageSize = 100;

  public String getStorageDir() {
    return storageDir;
  }

  public void setStorageDir(final String storageDir) {
    this.storageDir = storageDir;
  }

  public String[] getValidExtensions() {
    return validExtensions;
  }

  public void setValidExtensions(final String[] validExtensions) {
    this.validExtensions = validExtensions;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(final int pageSize) {
    this.pageSize = pageSize;
  }

}
