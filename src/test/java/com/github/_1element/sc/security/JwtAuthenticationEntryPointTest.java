package com.github._1element.sc.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class JwtAuthenticationEntryPointTest {

  @Test
  public void testCommence() throws Exception {
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint = new JwtAuthenticationEntryPoint();
    MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();

    jwtAuthenticationEntryPoint.commence(new MockHttpServletRequest(), httpServletResponse,null);

    assertEquals(401, httpServletResponse.getStatus());
    assertEquals("Unauthorized", httpServletResponse.getErrorMessage());
  }

}
