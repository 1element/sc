package com.github._1element.sc.dto; //NOSONAR

/**
 * Count of surveillance images.
 */
public class ImagesCountResult {

  private Long count;

  public ImagesCountResult(Long count) {
    this.count = count;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

}
