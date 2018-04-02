package com.github._1element.sc.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github._1element.sc.domain.CameraFtp;
import com.github._1element.sc.domain.CameraPicture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.events.ImageReceivedEvent;
import com.github._1element.sc.properties.ImageProperties;
import com.github._1element.sc.repository.SurveillanceImageRepository;

@RunWith(JUnit4.class)
public class SurveillanceImageHandlerServiceTest {

  @Mock
  private ThumbnailService thumbnailService;

  @Mock
  private SurveillanceImageRepository imageRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private FileService fileService;

  @InjectMocks
  private SurveillanceImageHandlerService surveillanceImageHandlerService;

  /**
   * Setup.
   */
  @Before
  public void setUp() throws Exception {
    ImageProperties imageProperties = new ImageProperties();
    imageProperties.setStorageDir("/tmp/sc-storage/");

    surveillanceImageHandlerService = new SurveillanceImageHandlerService(imageRepository, fileService,
        thumbnailService, imageProperties);

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testPopulateDestinationFileName() throws Exception {
    // arrange
    when(fileService.getUniquePrefix()).thenReturn("unique-prefix");

    byte[] imageData = "Image Data".getBytes();
    Camera testcamera1 = new Camera("testcamera1", null, null, null, null, null);
    ImageReceivedEvent imageReceivedEvent = new ImageReceivedEvent(imageData, testcamera1);

    // act
    String result = surveillanceImageHandlerService.populateDestinationFileName(imageReceivedEvent);

    // assert
    assertEquals("/tmp/sc-storage/unique-prefix-testcamera1.jpg", result);
  }

  @Test
  public void testHandleImageReceivedEvent() throws Exception {
    // arrange
    String expectedDestinationFileName = "/tmp/sc-storage/unique-prefix-testcamera1.jpg";
    when(fileService.getUniquePrefix()).thenReturn("unique-prefix");

    Path destinationPathMock = mock(Path.class);
    when(destinationPathMock.getFileName()).thenReturn(Paths.get(expectedDestinationFileName));
    when(fileService.getPath(expectedDestinationFileName)).thenReturn(destinationPathMock);

    byte[] imageData = "Image Data".getBytes();
    Camera testcamera1 = new Camera("testcamera1", null, null, null, null, null);
    ImageReceivedEvent imageReceivedEvent = new ImageReceivedEvent(imageData, testcamera1);

    // act
    surveillanceImageHandlerService.handleImageReceivedEvent(imageReceivedEvent);

    // assert
    verify(fileService).getPath(expectedDestinationFileName);
    verify(fileService).write(destinationPathMock, imageData);
    verify(thumbnailService).createThumbnail(destinationPathMock);
    verify(imageRepository).save(any(SurveillanceImage.class));
  }

}
