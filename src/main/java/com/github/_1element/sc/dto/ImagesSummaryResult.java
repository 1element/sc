package com.github._1element.sc.dto;

import java.util.Date;

/**
 * Summary of surveillance images (count for date).
 */
public class ImagesSummaryResult {

  private Date date;

  private Long count;

  public ImagesSummaryResult(Date date, Long count) {
    this.date = date;
    this.count = count;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

}
