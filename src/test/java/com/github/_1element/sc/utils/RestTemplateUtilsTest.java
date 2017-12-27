package com.github._1element.sc.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class RestTemplateUtilsTest {

  @Test
  public void testBuildWithAuthNoCredentials() throws Exception {
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    RestTemplate restTemplateResult = RestTemplateUtils.buildWithAuth(restTemplateBuilder, "http://host.example/path");

    assertEquals(0, restTemplateResult.getInterceptors().size());
  }

  @Test
  public void testBuildWithAuthCredentials() throws Exception {
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    RestTemplate restTemplateResult = RestTemplateUtils.buildWithAuth(restTemplateBuilder, "http://user:pass@host.example/command.cgi");

    assertTrue(restTemplateResult.getInterceptors().get(0) instanceof BasicAuthorizationInterceptor);
    assertEquals("user", ReflectionTestUtils.getField(restTemplateResult.getInterceptors().get(0), "username"));
    assertEquals("pass", ReflectionTestUtils.getField(restTemplateResult.getInterceptors().get(0), "password"));
  }

  @Test
  public void testBuildWithAuthEmptyPassword() throws Exception {
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    RestTemplate restTemplateResult = RestTemplateUtils.buildWithAuth(restTemplateBuilder, "http://username:@host.example/command.cgi");

    assertTrue(restTemplateResult.getInterceptors().get(0) instanceof BasicAuthorizationInterceptor);
    assertEquals("username", ReflectionTestUtils.getField(restTemplateResult.getInterceptors().get(0), "username"));
    assertEquals("", ReflectionTestUtils.getField(restTemplateResult.getInterceptors().get(0), "password"));
  }

  @Test
  public void testBuildWithAuthMissingPassword() throws Exception {
    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    RestTemplate restTemplateResult = RestTemplateUtils.buildWithAuth(restTemplateBuilder, "http://something@host.example/command.cgi");

    assertEquals(0, restTemplateResult.getInterceptors().size());
  }

}
