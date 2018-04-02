package com.github._1element.sc.dto; //NOSONAR

import java.time.LocalDateTime;

/**
 * DTO used to bulk update all {@link com.github._1element.sc.domain.SurveillanceImage} before a provided timestamp.
 */
public class SurveillanceImageBulkUpdateResource {

  private LocalDateTime dateBefore;

  private boolean archived;

  public LocalDateTime getDateBefore() {
    return dateBefore;
  }

  public void setDateBefore(final LocalDateTime dateBefore) {
    this.dateBefore = dateBefore;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(final boolean archived) {
    this.archived = archived;
  }

}
