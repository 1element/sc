package com.github._1element.sc.controller;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.dto.ImagesCameraSummaryResult;
import com.github._1element.sc.service.JwtAuthenticationService;
import com.github._1element.sc.service.SurveillanceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@AutoConfigureMockMvc
public class SurveillanceFeedControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtAuthenticationService jwtAuthenticationService;

  @MockBean
  private SurveillanceService surveillanceService;

  @Test
  public void testCamerasfeedUnauthorized() throws Exception {
    mockMvc.perform(get("/feed/cameras")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testCamerasFeed() throws Exception {
    // arrange
    LocalDateTime localDateTime = LocalDateTime.of(2018, 1, 20, 15, 45, 0, 0);
    Camera camera1 = new Camera("idCamera1", "Camera 1", null, null, null, null);
    ImagesCameraSummaryResult cameraSummaryResult1 = new ImagesCameraSummaryResult(camera1, 102L, localDateTime);

    Camera camera2 = new Camera("idCamera2", "Camera 2", null, null, null, null);
    ImagesCameraSummaryResult cameraSummaryResult2 = new ImagesCameraSummaryResult(camera2, 22L, localDateTime);

    given(surveillanceService.getImagesCameraSummary())
        .willReturn(Arrays.asList(cameraSummaryResult1, cameraSummaryResult2));

    Cookie tokenCookie = jwtAuthenticationService.generateTokenCookie("admin");

    // act and assert
    mockMvc.perform(get("/feed/cameras").cookie(tokenCookie))
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Camera 1</title>")))
        .andExpect(content().string(containsString("<description>102 (2018-01-20 15:45:00)</description>")))
        .andExpect(content().string(containsString("<title>Camera 2</title>")))
        .andExpect(content().string(containsString("<description>22 (2018-01-20 15:45:00)</description>")));
  }

}
