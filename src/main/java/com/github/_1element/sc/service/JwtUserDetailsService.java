package com.github._1element.sc.service;

import com.github._1element.sc.properties.SurveillanceSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * User details service class.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

  private SurveillanceSecurityProperties securityProperties;

  @Autowired
  public JwtUserDetailsService(SurveillanceSecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
  }

  /**
   * Locates the user based on the username.
   * There is currently only one single user defined in the application.properties file.
   *
   * @param username the username to load
   * @return a fully populated user record
   * @throws UsernameNotFoundException if user was not found
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Objects.requireNonNull(securityProperties.getUsername(), "Username must not be null. Check your configuration.");
    Objects.requireNonNull(securityProperties.getPassword(), "Password must not be null. Check your configuration.");
    Objects.requireNonNull(username, "Provided username must not be null.");

    if (securityProperties.getUsername().equals(username)) {
      return User.withUsername(securityProperties.getUsername()).password(securityProperties.getPassword())
        .roles("USER").build();
    }

    throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
  }

}
