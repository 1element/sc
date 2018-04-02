package com.github._1element.sc.dto; //NOSONAR

/**
 * Camera REST resource.
 * Similar to {@link com.github._1element.sc.domain.Camera} but with additional/limited attributes.
 * We do not want to expose our internal entity so this DTO is used.
 */
public class CameraResource {

  private String id;

  private String name;

  private String snapshotProxyUrl;

  private String streamGeneratorUrl;

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public String getSnapshotProxyUrl() {
    return snapshotProxyUrl;
  }

  public void setSnapshotProxyUrl(final String snapshotProxyUrl) {
    this.snapshotProxyUrl = snapshotProxyUrl;
  }

  public String getStreamGeneratorUrl() {
    return streamGeneratorUrl;
  }

  public void setStreamGeneratorUrl(final String streamGeneratorUrl) {
    this.streamGeneratorUrl = streamGeneratorUrl;
  }

}
