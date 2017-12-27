package com.github._1element.sc.controller; //NOSONAR

import com.github._1element.sc.security.JwtAuthenticationRequest;
import com.github._1element.sc.service.JwtAuthenticationService;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller to handle authentication login.
 */
@RestController
@RequestMapping(URIConstants.API_ROOT)
public class SurveillanceAuthenticationController {

  private JwtAuthenticationService jwtAuthenticationService;

  @Autowired
  public SurveillanceAuthenticationController(JwtAuthenticationService jwtAuthenticationService) {
    this.jwtAuthenticationService = jwtAuthenticationService;
  }

  /**
   * Endpoint to create an authentication token, that will be returned as an http-only cookie.
   *
   * @param authenticationRequest the authentication request with username and password
   * @param response the http response
   *
   * @throws AuthenticationException if authentication failed
   */
  @PostMapping(URIConstants.API_AUTH)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
                                                     HttpServletResponse response) throws AuthenticationException {

    Authentication authentication = jwtAuthenticationService.attemptAuthentication(authenticationRequest.getUsername(),
        authenticationRequest.getPassword());

    SecurityContextHolder.getContext().setAuthentication(authentication);

    Cookie cookie = jwtAuthenticationService.generateTokenCookie(authenticationRequest.getUsername());
    response.addCookie(cookie);
  }

}
