package com.github._1element.sc.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.dto.ImagesCameraSummaryResult;
import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.repository.SurveillanceImageRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class SurveillanceServiceTest {

  @Autowired
  private SurveillanceImageRepository imageRepository;

  @Autowired
  private CameraRepository cameraRepository;

  @Autowired
  private SurveillanceService surveillanceService;

  @Test
  public void testGetImagesCameraSummary() throws Exception {
    // test fixtures for testcamera1
    Camera camera1 = cameraRepository.findById("testcamera1");
    LocalDateTime time1 = LocalDateTime.of(2017, Month.JUNE, 2, 11, 18, 42);
    SurveillanceImage image1 = new SurveillanceImage("test-file1.jpg", camera1.getId(), time1);
    imageRepository.save(image1);

    LocalDateTime time2 = LocalDateTime.of(2017, Month.MAY, 3, 10, 15, 55);
    SurveillanceImage image2 = new SurveillanceImage("test-file2.jpg", camera1.getId(), time2);
    imageRepository.save(image2);

    // test fixtures for testcamera2
    Camera camera2 = cameraRepository.findById("testcamera2");
    LocalDateTime time3 = LocalDateTime.of(2017, Month.JUNE, 1, 8, 18, 44);
    SurveillanceImage image3 = new SurveillanceImage("test-file3.jpg", camera2.getId(), time3);
    imageRepository.save(image3);

    // expectations
    ImagesCameraSummaryResult expectedResultCamera1 = new ImagesCameraSummaryResult(camera1, 2L, time1);
    ImagesCameraSummaryResult expectedResultCamera2 = new ImagesCameraSummaryResult(camera2, 1L, time3);
    ImagesCameraSummaryResult expectedResultCamera3 = new ImagesCameraSummaryResult(cameraRepository.findById("testcamera3"), 0L, null);

    // assert
    List<ImagesCameraSummaryResult> result = surveillanceService.getImagesCameraSummary();

    assertNotNull(result);
    assertTrue(result.size() == 4);
    assertTrue(result.stream().anyMatch(e -> expectedResultCamera1.equals(e)));
    assertTrue(result.stream().anyMatch(e -> expectedResultCamera2.equals(e)));
    assertTrue(result.stream().anyMatch(e -> expectedResultCamera3.equals(e)));
  }

}
