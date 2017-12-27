package com.github._1element.sc.security; //NOSONAR

/**
 * Authentication request with username and password.
 */
public class JwtAuthenticationRequest {

  private String username;
  private String password;

  protected JwtAuthenticationRequest() {
  }

  public JwtAuthenticationRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

}
