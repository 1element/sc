package com.github._1element.sc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.security.JwtAuthenticationRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@AutoConfigureMockMvc
public class SurveillanceAuthenticationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private static final String AUTH_ENDPOINT = "/api/v1/auth";

  @Test
  public void testCreateAuthenticationToken() throws Exception {
    JwtAuthenticationRequest jwtAuthenticationRequest = new JwtAuthenticationRequest("admin", "password");

    mockMvc.perform(post(AUTH_ENDPOINT)
      .contentType(MediaType.APPLICATION_JSON)
      .content(new ObjectMapper().writeValueAsString(jwtAuthenticationRequest)))
      .andExpect(status().isNoContent());
  }

  @Test
  public void testCreateAuthenticationTokenWithInvalidPassword() throws Exception {
    JwtAuthenticationRequest jwtAuthenticationRequest = new JwtAuthenticationRequest("admin", "invalid-password");

    mockMvc.perform(post(AUTH_ENDPOINT)
      .contentType(MediaType.APPLICATION_JSON)
      .content(new ObjectMapper().writeValueAsString(jwtAuthenticationRequest)))
      .andExpect(status().isUnauthorized());
  }

  @Test
  public void testCreateAuthenticationTokenWithInvalidUsername() throws Exception {
    JwtAuthenticationRequest jwtAuthenticationRequest = new JwtAuthenticationRequest("invalid-username", "password");

    mockMvc.perform(post(AUTH_ENDPOINT)
      .contentType(MediaType.APPLICATION_JSON)
      .content(new ObjectMapper().writeValueAsString(jwtAuthenticationRequest)))
      .andExpect(status().isUnauthorized());
  }

}
