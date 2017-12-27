package com.github._1element.sc.security; //NOSONAR

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication entry point.
 * This will respond to all unauthorized requests with a 401 header.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException {

    // This is invoked when a user tries to access a secured REST resource without supplying any credentials.
    // We just send a 401 unauthorized response.
    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }

}
