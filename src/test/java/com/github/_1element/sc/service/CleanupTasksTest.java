package com.github._1element.sc.service;

import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.properties.ImageProperties;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class CleanupTasksTest {

  @Mock
  private SurveillanceImageRepository imageRepository;

  @Mock
  private ImageProperties imageProperties;

  @Mock
  private FileService fileService;

  @InjectMocks
  private CleanupTasks cleanupTasks;

  /**
   * Setup for all tests.
   *
   * @throws Exception exception in case of an error
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    // test data
    final SurveillanceImage image1 = new SurveillanceImage("file1.jpg", "camera1", LocalDateTime.now());
    final SurveillanceImage image2 = new SurveillanceImage("file2.jpg", "camera2", LocalDateTime.now());
    final List<SurveillanceImage> images = Arrays.asList(image1, image2);

    // mocking
    when(imageRepository.getArchivedImagesToCleanup(any())).thenReturn(images);
    when(imageProperties.getStorageDir()).thenReturn("/tmp/storage-dir/");

    cleanupTasks = new CleanupTasks(imageRepository, fileService, imageProperties);

    ReflectionTestUtils.setField(cleanupTasks, "keepHours", 24);
  }

  @Test
  public void testCleanupArchive() throws Exception {
    ReflectionTestUtils.setField(cleanupTasks, "isCleanupEnabled", true);

    cleanupTasks.cleanupArchive();

    // verify method calls to delete files (2 images + 2 thumbnails)
    verify(fileService, times(4)).delete(any(Path.class));

    // verify image repository db deletion for 2 images
    verify(imageRepository, times(2)).delete(any(SurveillanceImage.class));
  }

  @Test
  public void testCleanupArchiveDisabled() throws Exception {
    ReflectionTestUtils.setField(cleanupTasks, "isCleanupEnabled", false);

    cleanupTasks.cleanupArchive();

    verifyZeroInteractions(imageRepository);
  }

}
