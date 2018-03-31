package com.github._1element.sc.dto; //NOSONAR

import java.time.LocalDateTime;

/**
 * REST projection for the internal {@link com.github._1element.sc.domain.SurveillanceImage} entity.
 */
public class SurveillanceImageResource {

  private long id;

  private String fileName;

  private String cameraId;

  private String cameraName;

  private LocalDateTime receivedAt;

  private boolean archived;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getCameraId() {
    return cameraId;
  }

  public void setCameraId(String cameraId) {
    this.cameraId = cameraId;
  }

  public String getCameraName() {
    return cameraName;
  }

  public void setCameraName(String cameraName) {
    this.cameraName = cameraName;
  }

  public LocalDateTime getReceivedAt() {
    return receivedAt;
  }

  public void setReceivedAt(LocalDateTime receivedAt) {
    this.receivedAt = receivedAt;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

}
