package com.github._1element.sc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
  
  private static Camera camera1;
  
  private static Camera camera2;
  
  private static LocalDateTime time1;
  
  private static LocalDateTime time3;
  
  private static boolean fixturesCreated = false;
  
  @Before
  public void setUp() throws Exception {
    if (fixturesCreated) {
      return;
    }

    // test fixtures for testcamera1
    camera1 = cameraRepository.findById("testcamera1");
    time1 = LocalDateTime.of(2017, Month.JUNE, 2, 11, 18, 42);
    SurveillanceImage image1 = new SurveillanceImage("test-file1.jpg", camera1.getId(), time1);
    imageRepository.save(image1);

    LocalDateTime time2 = LocalDateTime.of(2017, Month.MAY, 3, 10, 15, 55);
    SurveillanceImage image2 = new SurveillanceImage("test-file2.jpg", camera1.getId(), time2);
    imageRepository.save(image2);

    // test fixtures for testcamera2
    camera2 = cameraRepository.findById("testcamera2");
    time3 = LocalDateTime.of(2017, Month.JUNE, 1, 8, 18, 44);
    SurveillanceImage image3 = new SurveillanceImage("test-file3.jpg", camera2.getId(), time3);
    imageRepository.save(image3);
    
    fixturesCreated = true;
  }

  @Test
  public void testGetImagesCameraSummary() throws Exception {
    // expected results
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

  @Test
  public void testGetMostRecentImageDate() throws Exception {
    LocalDateTime result = surveillanceService.getMostRecentImageDate();

    assertEquals(time1, result);
  }

  @Test
  public void testGetMostRecentImageDateForPage() throws Exception {
    Page<SurveillanceImage> images = surveillanceService.getImagesPage(Optional.of("testcamera1"), 
        Optional.empty(), false, new PageRequest(0, 10));
    
    LocalDateTime result = surveillanceService.getMostRecentImageDate(images, Optional.empty());

    assertEquals(time1, result);
  }

  @Test
  public void testGetMostRecentImageDateForPageWithDateFilter() throws Exception {
    Page<SurveillanceImage> images = surveillanceService.getImagesPage(Optional.of("testcamera1"), 
        Optional.empty(), false, new PageRequest(0, 10));

    LocalDateTime result = surveillanceService.getMostRecentImageDate(images, Optional.of(LocalDate.now()));

    assertNull(result);
  }

}
