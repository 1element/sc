package com.github._1element.sc.domain.remotecopy; //NOSONAR

/**
 * Interface for remote copy cleanup components.
 */
public interface RemoteCopyCleanup {

  /**
   * Remove old files. Should be scheduled.
   */
  void cleanup();

}
