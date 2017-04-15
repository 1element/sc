package com.github._1element.sc.service;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.repository.SurveillanceImageRepository;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest(CleanupTasks.class)
public class CleanupTasksTest {

  @Mock
  private SurveillanceImageRepository imageRepository;

  @InjectMocks
  private CleanupTasks cleanupTasks;

  @Before
  public void setUp() throws Exception {
    // configuration
    ReflectionTestUtils.setField(cleanupTasks, "keepHours", 24);
    ReflectionTestUtils.setField(cleanupTasks, "imageStorageDirectory", "/invalid/tmp/test/");

    // test data
    SurveillanceImage image1 = new SurveillanceImage("file1.jpg", "camera1", LocalDateTime.now());
    SurveillanceImage image2 = new SurveillanceImage("file2.jpg", "camera2", LocalDateTime.now());
    List<SurveillanceImage> images = Lists.newArrayList(image1, image2);

    // mocking
    MockitoAnnotations.initMocks(this);
    Mockito.when(imageRepository.getArchivedImagesToCleanup(any())).thenReturn(images);
    PowerMockito.mockStatic(Files.class);
  }

  @Test
  public void testCleanupArchive() throws Exception {
    ReflectionTestUtils.setField(cleanupTasks, "isCleanupEnabled", true);

    cleanupTasks.cleanupArchive();

    // verify static method calls to delete files (2 images + 2 thumbnails)
    PowerMockito.verifyStatic(times(4));
    Files.delete(any(Path.class));

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
