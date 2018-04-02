package com.github._1element.sc.dto; //NOSONAR

/**
 * DTO used to update {@link com.github._1element.sc.domain.SurveillanceImage}.
 */
public class SurveillanceImageUpdateResource {

  private long id;

  private boolean archived;

  public long getId() {
    return id;
  }

  public void setId(final long id) {
    this.id = id;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(final boolean archived) {
    this.archived = archived;
  }

}
