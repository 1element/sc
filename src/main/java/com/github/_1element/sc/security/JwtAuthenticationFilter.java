package com.github._1element.sc.security; //NOSONAR

import com.github._1element.sc.service.JwtAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Authentication filter, will be passed through for all requests.
 * If cookie with valid token exists authentication will be passed to the security context holder.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtAuthenticationService jwtAuthenticationService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    // gets valid authentication if cookie with token (JWT) exists
    Authentication authentication = jwtAuthenticationService.getAuthentication(request);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    chain.doFilter(request, response);
  }

}
