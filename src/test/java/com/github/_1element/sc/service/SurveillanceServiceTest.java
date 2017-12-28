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
import org.springframework.data.domain.Sort;
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

  private static SurveillanceImage image1;

  private static SurveillanceImage image2;

  private static SurveillanceImage image3;

  private static PageRequest pageRequest;

  private static boolean fixturesCreated = false;

  /**
   * Setup for all tests.
   *
   * @throws Exception exception in case of an error
   */
  @Before
  public void setUp() throws Exception {
    if (fixturesCreated) {
      return;
    }

    pageRequest = new PageRequest(0, 10, new Sort(new Sort.Order(Sort.Direction.DESC, "receivedAt")));

    // test fixtures for testcamera1
    camera1 = cameraRepository.findById("testcamera1");
    time1 = LocalDateTime.of(2017, Month.JUNE, 2, 11, 18, 42);
    image1 = new SurveillanceImage("test-file1.jpg", camera1.getId(), time1);
    imageRepository.save(image1);

    LocalDateTime time2 = LocalDateTime.of(2017, Month.MAY, 3, 10, 15, 55);
    image2 = new SurveillanceImage("test-file2.jpg", camera1.getId(), time2);
    imageRepository.save(image2);

    // test fixtures for testcamera2
    camera2 = cameraRepository.findById("testcamera2");
    time3 = LocalDateTime.of(2017, Month.JUNE, 1, 8, 18, 44);
    image3 = new SurveillanceImage("test-file3.jpg", camera2.getId(), time3);
    imageRepository.save(image3);

    fixturesCreated = true;
  }

  @Test
  public void testGetImagesCameraSummary() throws Exception {
    // expected results
    final ImagesCameraSummaryResult expectedResultCamera1 = new ImagesCameraSummaryResult(camera1, 2L, time1);
    final ImagesCameraSummaryResult expectedResultCamera2 = new ImagesCameraSummaryResult(camera2, 1L, time3);
    final ImagesCameraSummaryResult expectedResultCamera3 = new ImagesCameraSummaryResult(
        cameraRepository.findById("testcamera3"), 0L, null);

    // assert
    List<ImagesCameraSummaryResult> result = surveillanceService.getImagesCameraSummary();

    assertNotNull(result);
    assertTrue(result.size() == 4);
    assertTrue(result.stream().anyMatch(e -> expectedResultCamera1.equals(e)));
    assertTrue(result.stream().anyMatch(e -> expectedResultCamera2.equals(e)));
    assertTrue(result.stream().anyMatch(e -> expectedResultCamera3.equals(e)));
  }

  @Test
  public void testGetImagesPageForCamera() throws Exception {
    Optional<String> nameCamera1 = Optional.of("testcamera1");
    Optional<LocalDate> emptyDate = Optional.empty();

    Page<SurveillanceImage> result = surveillanceService.getImagesPage(nameCamera1, emptyDate, false, pageRequest);
    List<SurveillanceImage> contentResult = result.getContent();

    assertTrue(contentResult.size() == 2);
    assertTrue(contentResult.stream().anyMatch(e -> image1.equals(e)));
    assertTrue(contentResult.stream().anyMatch(e -> image2.equals(e)));
  }

  @Test
  public void testGetImagesPageForCameraAndDate() throws Exception {
    Optional<String> nameCamera1 = Optional.of("testcamera1");
    Optional<LocalDate> date = Optional.of(LocalDate.of(2017, Month.JUNE, 2));

    Page<SurveillanceImage> result = surveillanceService.getImagesPage(nameCamera1, date, false, pageRequest);
    List<SurveillanceImage> contentResult = result.getContent();

    assertTrue(contentResult.size() == 1);
    assertTrue(contentResult.stream().anyMatch(e -> image1.equals(e)));
  }

  @Test
  public void testGetImagesPagesWithoutFilter() throws Exception {
    Page<SurveillanceImage> result = surveillanceService.getImagesPage(Optional.empty(), Optional.empty(),
        false, pageRequest);
    List<SurveillanceImage> contentResult = result.getContent();

    assertTrue(contentResult.size() == 3);
    assertTrue(contentResult.stream().anyMatch(e -> image1.equals(e)));
    assertTrue(contentResult.stream().anyMatch(e -> image2.equals(e)));
    assertTrue(contentResult.stream().anyMatch(e -> image3.equals(e)));
  }

}
