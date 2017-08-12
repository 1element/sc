package com.github._1element.sc.domain; //NOSONAR

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Surveillance image entity.
 */
@Entity
public class SurveillanceImage {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  private String fileName;

  private String cameraId;

  private LocalDateTime receivedAt;

  private boolean archived = false;

  protected SurveillanceImage() {
  }

  public SurveillanceImage(String fileName, String cameraId, LocalDateTime receivedAt) {
    this.fileName = fileName;
    this.cameraId = cameraId;
    this.receivedAt = receivedAt;
  }

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

  public String getCameraId() {
    return cameraId;
  }

  public void setCameraId(String cameraId) {
    this.cameraId = cameraId;
  }

  @Override
  public String toString() {
    return String.valueOf(id);
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof SurveillanceImage)) {
      return false;
    }
    SurveillanceImage castOther = (SurveillanceImage) other;
    return Objects.equals(id, castOther.id) && Objects.equals(fileName, castOther.fileName)
        && Objects.equals(cameraId, castOther.cameraId) && Objects.equals(receivedAt, castOther.receivedAt)
        && Objects.equals(archived, castOther.archived);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, fileName, cameraId, receivedAt, archived);
  }

}
