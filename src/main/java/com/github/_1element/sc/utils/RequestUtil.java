package com.github._1element.sc.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * Request utility class.
 */
public final class RequestUtil {

  private static final String HEADER_REQUESTED_WITH = "X-Requested-With";

  private static final String XML_HTTP_REQUEST = "XMLHttpRequest";

  private RequestUtil() {
    // hide constructor for static utility class
  }

  /**
   * Returns true if it is an ajax request.
   *
   * @param request request to check
   * @return
   */
  public static boolean isAjax(HttpServletRequest request) {
    String requestedWithHeader = request.getHeader(HEADER_REQUESTED_WITH);

    return XML_HTTP_REQUEST.equals(requestedWithHeader);
  }

}
