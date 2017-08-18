package com.github._1element.sc.dto; //NOSONAR

import java.util.Date;

/**
 * Summary (count) of surveillance images for each date.
 */
public class ImagesDateSummaryResult {

  private Date date;

  private Long count;

  public ImagesDateSummaryResult(Date date, Long count) {
    this.date = date;
    this.count = count;
  }

  public Date getDate() {
    return date;
  }

  public Long getCount() {
    return count;
  }

}
