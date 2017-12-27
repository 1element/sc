package com.github._1element.sc.properties; //NOSONAR

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for handling authorization.
 */
@Component
@ConfigurationProperties("sc.security")
public class SurveillanceSecurityProperties {

  private String username;

  private String password;

  private String secret;

  private String cookieName = "JWT";

  private int tokenExpiration = 2592000;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public String getCookieName() {
    return cookieName;
  }

  public void setCookieName(String cookieName) {
    this.cookieName = cookieName;
  }

  public int getTokenExpiration() {
    return tokenExpiration;
  }

  public void setTokenExpiration(int tokenExpiration) {
    this.tokenExpiration = tokenExpiration;
  }

}
