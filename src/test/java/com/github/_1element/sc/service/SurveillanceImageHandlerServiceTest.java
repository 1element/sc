package com.github._1element.sc.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.ApplicationEventPublisher;

import com.github._1element.sc.domain.Camera;
import com.github._1element.sc.domain.SurveillanceImage;
import com.github._1element.sc.events.ImageReceivedEvent;
import com.github._1element.sc.properties.ImageProperties;
import com.github._1element.sc.repository.SurveillanceImageRepository;

@RunWith(JUnit4.class)
public class SurveillanceImageHandlerServiceTest {

  @Mock
  private FileService fileService;
  
  @Mock
  private ThumbnailService thumbnailService;
  
  @Mock
  private SurveillanceImageRepository imageRepository;
  
  @Mock
  private CounterService counterService;

  @Mock
  private ApplicationEventPublisher eventPublisher;
 
  @InjectMocks
  private SurveillanceImageHandlerService surveillanceImageHandlerService;

  private static Camera testcamera1;
  
  private static final String SOURCE_TESTFILE_NAME = "incoming-testfile.jpg";
  
  private static final String DESTINATION_TESTFILE_PATH = "/tmp/sc-storage/null-testcamera1-incoming-testfile.jpg";
  
  @Before
  public void setUp() throws Exception {
    testcamera1 = new Camera("testcamera1", null, null, null, null, null, null, null, null);

    ImageProperties imageProperties = new ImageProperties();
    imageProperties.setStorageDir("/tmp/sc-storage/");

    surveillanceImageHandlerService = new SurveillanceImageHandlerService(imageRepository, fileService, thumbnailService, imageProperties);

    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testHandleImageReceivedEvent() throws Exception {
    // prepare mocks
    File sourceFileMock = mock(File.class);
    Mockito.when(sourceFileMock.getName()).thenReturn(SOURCE_TESTFILE_NAME);

    File destinationFileMock = mock(File.class);
    Mockito.when(destinationFileMock.getName()).thenReturn("filename.jpg");

    Mockito.when(fileService.createFile(SOURCE_TESTFILE_NAME)).thenReturn(sourceFileMock);
    Mockito.when(fileService.createFile(DESTINATION_TESTFILE_PATH)).thenReturn(destinationFileMock);

    // execute
    ImageReceivedEvent imageReceivedEvent = new ImageReceivedEvent(SOURCE_TESTFILE_NAME, testcamera1);
    surveillanceImageHandlerService.handleImageReceivedEvent(imageReceivedEvent);

    // verify behaviour
    verify(fileService).createFile(eq(SOURCE_TESTFILE_NAME));
    verify(fileService).createFile(eq(DESTINATION_TESTFILE_PATH));
    verify(fileService).moveFile(eq(sourceFileMock), eq(destinationFileMock));
    verify(imageRepository).save(any(SurveillanceImage.class));
  }

}
