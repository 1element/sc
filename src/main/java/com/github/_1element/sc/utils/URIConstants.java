package com.github._1element.sc.utils; //NOSONAR

/**
 * Constants for URI building.
 */
public final class URIConstants {

  public static final String ROOT = "/";

  public static final String RECORDINGS = "/recordings";

  public static final String LIVEVIEW = "/liveview";

  public static final String LIVESTREAM = "/livestream";

  public static final String SETTINGS = "/settings";

  public static final String FEED_ROOT = "/feed";

  public static final String FEED_STATUS = "/status";
  
  public static final String FEED_CAMERAS = "/cameras";

  public static final String API_ROOT = "/api/v1";
  
  public static final String API_RECORDINGS = "/recordings";

  public static final String API_RECORDINGS_COUNT = "/recordings/count";

  public static final String API_PUSH_NOTIFICATION_SETTINGS = "/push-notification-settings";

  public static final String TRANSFORM_MJPEG = "/transform/mjpeg/{id}";

  private URIConstants() {
    // hide constructor
  }

}
