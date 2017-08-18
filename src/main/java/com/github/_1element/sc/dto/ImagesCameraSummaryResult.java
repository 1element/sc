package com.github._1element.sc.dto; //NOSONAR

import java.time.LocalDateTime;

import com.github._1element.sc.domain.Camera;
import java.util.Objects;

/**
 * Summary of surveillance images for each camera (count and most recent image date).
 */
public class ImagesCameraSummaryResult {

  private Camera camera;
  
  private Long count;

  private LocalDateTime mostRecentDate;

  public ImagesCameraSummaryResult(Camera camera, Long count, LocalDateTime mostRecentDate) {
    this.camera = camera;
    this.count = count;
    this.mostRecentDate = mostRecentDate;
  }

  public Camera getCamera() {
    return camera;
  }

  public Long getCount() {
    return count;
  }

  public LocalDateTime getMostRecentDate() {
    return mostRecentDate;
  }

  @Override
  public boolean equals(final Object other) {
    if (!(other instanceof ImagesCameraSummaryResult)) {
      return false;
    }
    ImagesCameraSummaryResult castOther = (ImagesCameraSummaryResult) other;
    return Objects.equals(camera, castOther.camera) && Objects.equals(count, castOther.count)
        && Objects.equals(mostRecentDate, castOther.mostRecentDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(camera, count, mostRecentDate);
  }

}
