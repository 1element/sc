package com.github._1element.sc.domain; //NOSONAR

import org.apache.commons.lang3.StringUtils;

/**
 * Camera picture value object.
 */
public class CameraPicture {

  private String snapshotUrl;
  private boolean snapshotEnabled;
  private boolean streamEnabled;

  /**
   * Constructs a new camera picture value object.
   *
   * @param snapshotUrl optional url to retrieve snapshots
   * @param snapshotEnabled true if snapshots are enabled
   * @param streamEnabled true if streaming is enabled
   */
  public CameraPicture(String snapshotUrl, boolean snapshotEnabled, boolean streamEnabled) {
    if ((snapshotEnabled || streamEnabled) && StringUtils.isBlank(snapshotUrl)) {
      throw new IllegalArgumentException("Snapshot-url must be provided if snapshot-enabled or stream-enabled.");
    }

    this.snapshotUrl = snapshotUrl;
    this.snapshotEnabled = snapshotEnabled;
    this.streamEnabled = streamEnabled;
  }

  public String getSnapshotUrl() {
    return snapshotUrl;
  }

  public boolean isSnapshotEnabled() {
    return snapshotEnabled;
  }

  public boolean isStreamEnabled() {
    return streamEnabled;
  }

}
