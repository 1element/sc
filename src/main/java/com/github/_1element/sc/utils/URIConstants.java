package com.github._1element.sc.utils; //NOSONAR

/**
 * Constants for URI building.
 */
public final class URIConstants {

  // Web
  public static final String ROOT = "/";

  public static final String RECORDINGS = "/recordings";

  public static final String LIVEVIEW = "/liveview";

  public static final String SETTINGS = "/settings";

  // Generation
  public static final String GENERATE_ROOT = "/generate";

  public static final String GENERATE_MJPEG = "/mjpeg/{id}";

  // Proxy
  public static final String PROXY_ROOT = "/proxy";

  public static final String PROXY_SNAPSHOT = "/snapshot/{id}";

  // Feed
  public static final String FEED_ROOT = "/feed";

  public static final String FEED_CAMERAS = "/cameras";

  // API
  public static final String API_ROOT = "/api/v1";

  public static final String API_RECORDINGS = "/recordings";

  public static final String API_RECORDINGS_COUNT = "/recordings/count";

  public static final String API_CAMERAS = "/cameras";

  public static final String API_PUSH_NOTIFICATION_SETTINGS = "/push-notification-settings";

  public static final String API_PROPERTIES = "/properties";

  public static final String API_AUTH = "/auth";

  private URIConstants() {
    // hide constructor
  }

}
