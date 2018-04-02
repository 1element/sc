package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.properties.SurveillanceSecurityProperties;
import com.google.common.annotations.VisibleForTesting;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Authentication service based on JSON Web Tokens (JWT).
 * Instead of an authorization header we use an http-only cookie approach here to simplify the protection
 * of the static image resources.
 */
@Service
public class JwtAuthenticationService {

  private final UserDetailsService userDetailsService;

  private final AuthenticationManager authenticationManager;

  private final SurveillanceSecurityProperties securityProperties;

  private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationService.class);

  /**
   * Constructor.
   *
   * @param userDetailsService the user details service to use
   * @param authenticationManager the authentication manager to use
   * @param securityProperties the security properties to use (secret, cookie-name, expiration, etc.)
   */
  @Autowired
  public JwtAuthenticationService(final UserDetailsService userDetailsService,
                                  final AuthenticationManager authenticationManager,
                                  final SurveillanceSecurityProperties securityProperties) {
    this.userDetailsService = userDetailsService;
    this.authenticationManager = authenticationManager;
    this.securityProperties = securityProperties;
  }

  /**
   * Get token from request (cookie) and validate it.
   *
   * @param request the http request
   * @return Authentication if user is legitimated, otherwise null
   */
  public Authentication getAuthentication(final HttpServletRequest request) {
    final String token = getTokenFromCookie(request);

    if (token != null) {
      String username = null;
      try {
        username = getUsernameFromToken(token);
      } catch (final ExpiredJwtException exception) {
        LOG.warn("The token is expired and not valid anymore: {}", exception.getMessage());
      } catch (MalformedJwtException | SignatureException exception) {
        LOG.warn("An error occurred while parsing JWT token: {]", exception.getMessage());
      }

      // if a username was returned the token is valid
      if (username != null) {
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
      }
    }

    return null;
  }

  /**
   * Tries to attempt authentication for the provided username and password.
   *
   * @param username the username to authenticate
   * @param password the password to authenticate
   *
   * @return Authentication if credentials are valid, exception otherwise
   * @throws AuthenticationException if authentication failed
   */
  public Authentication attemptAuthentication(final String username, final String password)
      throws AuthenticationException {
    return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
  }

  /**
   * Generates a cookie with a JWT token for the given username.
   *
   * @param username the username to generate token for
   * @return cookie
   */
  public Cookie generateTokenCookie(final String username) {
    final String token = generateToken(username);
    return generateCookie(token);
  }

  /**
   * Parses and validates the username for the provided token.
   *
   * @param token the token to parse
   * @return username
   */
  @VisibleForTesting
  String getUsernameFromToken(final String token) {
    return Jwts.parser()
      .setSigningKey(securityProperties.getSecret())
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }

  /**
   * Generates a token for the provided username.
   *
   * @param username the username to generate token for
   * @return token
   */
  @VisibleForTesting
  String generateToken(final String username) {
    final Date createdDate = new Date();
    final Date expirationDate = calculateExpirationDate(createdDate);

    return Jwts.builder()
      .setSubject(username)
      .setIssuedAt(createdDate)
      .setExpiration(expirationDate)
      .signWith(SignatureAlgorithm.HS512, securityProperties.getSecret())
      .compact();
  }

  /**
   * Calculates the expiration date based on the creation date.
   *
   * @param createdDate the creation date
   * @return expiration date
   */
  @VisibleForTesting
  Date calculateExpirationDate(final Date createdDate) {
    return new Date(createdDate.getTime() + securityProperties.getTokenExpiration() * 1000L);
  }

  /**
   * Retrieves the token from the cookie.
   *
   * @param httpServletRequest the http request to read cookie from
   * @return token
   */
  @VisibleForTesting
  String getTokenFromCookie(final HttpServletRequest httpServletRequest) {
    final Cookie cookie = WebUtils.getCookie(httpServletRequest, securityProperties.getCookieName());
    return cookie != null ? cookie.getValue() : null;
  }

  /**
   * Generates a cookie for the provided token.
   *
   * @param token the token to use
   * @return cookie
   */
  @VisibleForTesting
  Cookie generateCookie(final String token) {
    final Cookie cookie = new Cookie(securityProperties.getCookieName(), token);
    cookie.setSecure(false);
    cookie.setHttpOnly(true);
    cookie.setMaxAge(securityProperties.getTokenExpiration());
    cookie.setPath("/");

    return cookie;
  }

}
