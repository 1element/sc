package com.github._1element.sc.service;

import com.github._1element.sc.SurveillanceCenterApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.Cookie;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class JwtAuthenticationServiceTest {

  @Autowired
  private JwtAuthenticationService jwtAuthenticationService;

  private static final String VALID_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTUxNjUyOTcxMywiZX"
      + "hwIjoyNDYzMjM4MjczfQ.8iiLCJQBU5xXoiUlPdSkPOaPKuxAPgDdT8Sil_N4l-NVqI5ojgnaVrNYi3DE16taKKpUvnhw3fbfcu2C3T9-XQ";

  private static final String EXPIRED_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTUxNjUzMDc3MywiZXh"
      + "wIjoxNTE2NTMwNzc0fQ.n16Udn2gYn6FdUOlx3OJuGrZ4yP2wPMeUx3mkscsQnNOwfiBPOB_kuz38BMrjREYKLPPPBJs_4aasbmqKwY99w";

  private static final String MALFORMED_TOKEN = "eyJhbGciOiJIUzUxMiJ9._totallyMalformed";

  private static final String TOKEN_NAME = "JWT";

  @Test
  public void testGenerateCookie() throws Exception {
    Cookie cookieResult = jwtAuthenticationService.generateCookie("t0ken");

    assertEquals(TOKEN_NAME, cookieResult.getName());
    assertEquals("t0ken", cookieResult.getValue());
    assertEquals("/", cookieResult.getPath());
    assertEquals(86400, cookieResult.getMaxAge());
  }

  @Test
  public void testGetTokenFromCookie() throws Exception {
    // arrange
    Cookie cookie = new Cookie(TOKEN_NAME, "tokenValue");
    cookie.setSecure(false);
    cookie.setHttpOnly(true);
    cookie.setPath("/");

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(cookie);

    // act
    String tokenResult = jwtAuthenticationService.getTokenFromCookie(request);

    // assert
    assertEquals("tokenValue", tokenResult);
  }

  @Test
  public void testGetTokenFromCookieFalseName() throws Exception {
    Cookie cookie = new Cookie("Token", "tokenValue");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(cookie);

    String tokenResult = jwtAuthenticationService.getTokenFromCookie(request);

    assertNull(tokenResult);
  }

  @Test
  public void testCalculateExpirationDate() throws Exception {
    Date expirationDate = jwtAuthenticationService.calculateExpirationDate(Date.from(
        Instant.ofEpochMilli(1516528858572L)));

    assertEquals(1516615258572L, expirationDate.getTime());
  }

  @Test
  public void testGenerateToken() throws Exception {
    String token = jwtAuthenticationService.generateToken("admin");

    // token depends on the current creation date, so just some rudimental asserts for now
    assertNotNull(token);
    assertEquals(174, token.length());
  }

  @Test
  public void testGetUsernameFromToken() throws Exception {
    String username = jwtAuthenticationService.getUsernameFromToken(VALID_TOKEN);

    assertEquals("admin", username);
  }

  @Test
  public void testGenerateTokenCookie() throws Exception {
    Cookie cookie = jwtAuthenticationService.generateTokenCookie("admin");

    assertEquals(TOKEN_NAME, cookie.getName());
    assertEquals("/", cookie.getPath());
    assertNotNull(cookie.getValue());
  }

  @Test(expected = BadCredentialsException.class)
  public void testAttemptAuthenticationWrongPassword() throws Exception {
    jwtAuthenticationService.attemptAuthentication("admin", "wrong-password");
  }

  @Test(expected = BadCredentialsException.class)
  public void testAttemptAuthenticationWrongUsername() throws Exception {
    jwtAuthenticationService.attemptAuthentication("adminuser", "password");
  }

  @Test
  public void testAttemptAuthentication() throws Exception {
    Authentication authentication = jwtAuthenticationService.attemptAuthentication("admin", "password");

    assertNotNull(authentication);
    assertEquals("admin", authentication.getName());
  }

  @Test
  public void testGetAuthentication() throws Exception {
    // arrange
    Cookie cookie = new Cookie(TOKEN_NAME, VALID_TOKEN);
    cookie.setSecure(false);
    cookie.setHttpOnly(true);
    cookie.setPath("/");

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(cookie);

    // act
    Authentication authentication = jwtAuthenticationService.getAuthentication(request);

    // assert
    assertNotNull(authentication);
    assertEquals("admin", authentication.getName());
  }

  @Test
  public void testGetAuthenticationExpiredToken() throws Exception {
    // arrange
    Cookie cookie = new Cookie(TOKEN_NAME, EXPIRED_TOKEN);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(cookie);

    // act
    Authentication authentication = jwtAuthenticationService.getAuthentication(request);

    // assert
    assertNull(authentication);
  }

  @Test
  public void testGetAuthenticationMalformedToken() throws Exception {
    // arrange
    Cookie cookie = new Cookie(TOKEN_NAME, MALFORMED_TOKEN);

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setCookies(cookie);

    // act
    Authentication authentication = jwtAuthenticationService.getAuthentication(request);

    // assert
    assertNull(authentication);
  }

  @Test
  public void testGetAuthenticationNoCookie() throws Exception {
    Authentication authentication = jwtAuthenticationService.getAuthentication(new MockHttpServletRequest());

    assertNull(authentication);
  }

}
