package com.github._1element.sc.controller;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.domain.PushNotificationSetting;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.repository.PushNotificationSettingRepository;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.github._1element.sc.service.JwtAuthenticationService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@AutoConfigureMockMvc
public class SurveillanceApiControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JwtAuthenticationService jwtAuthenticationService;

  @MockBean
  private SurveillanceImageRepository imageRepository;

  @MockBean
  private PushNotificationSettingRepository pushNotificationSettingRepository;

  private Cookie tokenCookie;

  private SurveillanceImage surveillanceImage;

  private SurveillanceImage surveillanceImage2;

  /**
   * Setup test fixtures.
   */
  @Before
  public void setUp() {
    tokenCookie = jwtAuthenticationService.generateTokenCookie("admin");

    LocalDateTime localDateTime = LocalDateTime.of(2018, 1, 27, 12, 48, 45, 0);
    surveillanceImage = new SurveillanceImage("filename.jpg", "camera1", localDateTime);
    surveillanceImage2 = new SurveillanceImage("filename2.jpg", "camera2", localDateTime);
  }

  @Test
  public void testRecordingUnauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/recordings/22")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testRecordingNotFound() throws Exception {
    given(imageRepository.findOne(eq(1L))).willReturn(null);

    mockMvc.perform(get("/api/v1/recordings/47").cookie(tokenCookie)).andExpect(status().isNotFound());
  }

  @Test
  public void testRecording() throws Exception {
    given(imageRepository.findOne(eq(1L))).willReturn(surveillanceImage);

    mockMvc.perform(get("/api/v1/recordings/1").cookie(tokenCookie))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is(0)))
      .andExpect(jsonPath("$.fileName", is("filename.jpg")))
      .andExpect(jsonPath("$.cameraId", is("camera1")))
      .andExpect(jsonPath("$.receivedAt", is("2018-01-27T12:48:45")))
      .andExpect(jsonPath("$.archived", is(false)));
  }

  @Test
  public void testRecordingsListUnauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/recordings")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testRecordingsList() throws Exception {
    Page<SurveillanceImage> pageResult = new PageImpl<>(Arrays.asList(surveillanceImage, surveillanceImage2), null, 2);
    given(imageRepository.findAllByArchived(anyBoolean(), any(Pageable.class))).willReturn(pageResult);

    mockMvc.perform(get("/api/v1/recordings").cookie(tokenCookie))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.surveillanceImageList[0].fileName", is("filename.jpg")))
      .andExpect(jsonPath("$._embedded.surveillanceImageList[0].cameraId", is("camera1")))
      .andExpect(jsonPath("$._embedded.surveillanceImageList[0].receivedAt", is("2018-01-27T12:48:45")))
      .andExpect(jsonPath("$._embedded.surveillanceImageList[0].archived", is(false)))
      .andExpect(jsonPath("$._embedded.surveillanceImageList[1].fileName", is("filename2.jpg")))
      .andExpect(jsonPath("$._embedded.surveillanceImageList[1].cameraId", is("camera2")))
      .andExpect(jsonPath("$._embedded.surveillanceImageList[1].receivedAt", is("2018-01-27T12:48:45")))
      .andExpect(jsonPath("$._embedded.surveillanceImageList[1].archived", is(false)))
      .andExpect(jsonPath("$.page.totalElements", is(2)))
      .andExpect(jsonPath("$.page.totalPages", is(1)))
      .andExpect(jsonPath("$._links.self.href", is("http://localhost/api/v1/recordings?page=0&size=100&archive=false")));
  }

  @Test
  public void testCamerasListUnauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/cameras")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testCamerasList() throws Exception {
    mockMvc.perform(get("/api/v1/cameras").cookie(tokenCookie))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].id", is("testcamera1")))
      .andExpect(jsonPath("$[0].name", is("Front door")))
      .andExpect(jsonPath("$[0].snapshotProxyUrl", is("http://localhost/proxy/snapshot/testcamera1")))
      .andExpect(jsonPath("$[0].streamGeneratorUrl", is("http://localhost/generate/mjpeg/testcamera1")))
      .andExpect(jsonPath("$[1].id", is("testcamera2")))
      .andExpect(jsonPath("$[1].name", is("Backyard")))
      .andExpect(jsonPath("$[1].snapshotProxyUrl", is("http://localhost/proxy/snapshot/testcamera2")))
      .andExpect(jsonPath("$[1].streamGeneratorUrl", is("http://localhost/generate/mjpeg/testcamera2")));
  }

  @Test
  public void testCameraUnauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/cameras/testcamera1")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testCamera() throws Exception {
    mockMvc.perform(get("/api/v1/cameras/testcamera1").cookie(tokenCookie))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.id", is("testcamera1")))
      .andExpect(jsonPath("$.name", is("Front door")))
      .andExpect(jsonPath("$.snapshotProxyUrl", is("http://localhost/proxy/snapshot/testcamera1")))
      .andExpect(jsonPath("$.streamGeneratorUrl", is("http://localhost/generate/mjpeg/testcamera1")));
  }

  @Test
  public void testPropertiesUnauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/properties")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testProperties() throws Exception {
    mockMvc.perform(get("/api/v1/properties").cookie(tokenCookie))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.imageThumbnailPrefix", is("thumbnail.")))
      .andExpect(jsonPath("$.imageBaseUrl", is("http://localhost/images/")));
  }

  @Test
  public void testPushNotificationSettingsListUnauthorized() throws Exception {
    mockMvc.perform(get("/api/v1/push-notification-settings")).andExpect(status().isUnauthorized());
  }

  @Test
  public void testPushNotificationSettingsList() throws Exception {
    mockMvc.perform(get("/api/v1/push-notification-settings").cookie(tokenCookie))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].cameraId", is("testcamera1")))
      .andExpect(jsonPath("$[0].cameraName", is("Front door")))
      .andExpect(jsonPath("$[0].enabled", is(false)))
      .andExpect(jsonPath("$[1].cameraId", is("testcamera2")))
      .andExpect(jsonPath("$[1].cameraName", is("Backyard")))
      .andExpect(jsonPath("$[1].enabled", is(false)));
  }

  @Test
  public void testRecordingsUpdate() throws Exception {
    // arrange
    JSONObject jsonUpdateResource1 = new JSONObject();
    jsonUpdateResource1.put("id", 1);
    jsonUpdateResource1.put("archived", false);
    JSONObject jsonUpdateResource2 = new JSONObject();
    jsonUpdateResource2.put("id", 2);
    jsonUpdateResource2.put("archived", false);

    JSONArray jsonPayload = new JSONArray();
    jsonPayload.put(jsonUpdateResource1);
    jsonPayload.put(jsonUpdateResource2);

    // act + assert
    mockMvc.perform(patch("/api/v1/recordings")
      .content(jsonPayload.toString())
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .cookie(tokenCookie))
      .andExpect(status().isNoContent());

    verify(imageRepository).updateSetArchived(Arrays.asList(1L, 2L));
  }

  @Test
  public void testRecordingsBulkUpdate() throws Exception {
    // arrange
    LocalDateTime dateBefore = LocalDateTime.now();
    JSONObject jsonPayload = new JSONObject();
    jsonPayload.put("dateBefore", dateBefore.toString());
    jsonPayload.put("archived", false);

    // act + assert
    mockMvc.perform(post("/api/v1/recordings")
      .content(jsonPayload.toString())
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .cookie(tokenCookie))
      .andExpect(status().isNoContent());

    verify(imageRepository).updateArchiveState(false, dateBefore);
  }

  @Test
  public void testPushNotificationSettingsUpdateExisting() throws Exception {
    // arrange
    String cameraId = "testcamera1";
    JSONObject jsonPayload = new JSONObject();
    jsonPayload.put("cameraId", cameraId);
    jsonPayload.put("enabled", true);
    PushNotificationSetting pushNotificationSetting = new PushNotificationSetting(cameraId, true);
    when(pushNotificationSettingRepository.findByCameraId(cameraId)).thenReturn(pushNotificationSetting);

    // act + assert
    mockMvc.perform(patch("/api/v1/push-notification-settings/" + cameraId)
      .content(jsonPayload.toString())
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .cookie(tokenCookie))
      .andExpect(status().isNoContent());

    verify(pushNotificationSettingRepository).save(refEq(pushNotificationSetting));
  }

  @Test
  public void testPushNotificationSettingsUpdateNonExisting() throws Exception {
    // arrange
    String cameraId = "testcamera1";
    JSONObject jsonPayload = new JSONObject();
    jsonPayload.put("cameraId", cameraId);
    jsonPayload.put("enabled", true);
    PushNotificationSetting expectedPushNotificationSetting = new PushNotificationSetting(cameraId, true);

    // act + assert
    mockMvc.perform(patch("/api/v1/push-notification-settings/" + cameraId)
      .content(jsonPayload.toString())
      .contentType(MediaType.APPLICATION_JSON_VALUE)
      .cookie(tokenCookie))
      .andExpect(status().isNoContent());

    verify(pushNotificationSettingRepository).save(refEq(expectedPushNotificationSetting));
  }

}
