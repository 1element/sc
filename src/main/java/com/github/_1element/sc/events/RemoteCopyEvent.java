package com.github._1element.sc.events; //NOSONAR

/**
 * Event to trigger remote copy.
 */
public class RemoteCopyEvent {

  private String fileName;

  public RemoteCopyEvent(String fileName) {
    this.fileName = fileName;
  }

  public String getFileName() {
    return fileName;
  }

}
