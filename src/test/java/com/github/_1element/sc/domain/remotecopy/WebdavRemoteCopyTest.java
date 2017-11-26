package com.github._1element.sc.domain.remotecopy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.service.FileService;
import com.github.sardine.Sardine;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class WebdavRemoteCopyTest {

  @Mock
  private Sardine sardine;

  @Mock
  private FileService fileService;

  @Autowired
  @InjectMocks
  private WebdavRemoteCopy webdavRemoteCopy;

  private static final String EXPECTED_LOCAL_FILE_PATH = "/tmp/test/local-file.jpg";

  @Test
  public void testHandle() throws Exception {
    // mocking
    MockitoAnnotations.initMocks(this);
    Path pathMock = mock(Path.class);
    Mockito.when(pathMock.getFileName()).thenReturn(Paths.get("local-file.jpg"));
    Mockito.when(fileService.getPath(EXPECTED_LOCAL_FILE_PATH)).thenReturn(pathMock);

    InputStream inputStreamMock = mock(InputStream.class);
    Mockito.when(fileService.createInputStream(any(Path.class))).thenReturn(inputStreamMock);

    // execute and verify
    RemoteCopyEvent remoteCopyEvent = new RemoteCopyEvent(EXPECTED_LOCAL_FILE_PATH);
    webdavRemoteCopy.handle(remoteCopyEvent);

    verify(sardine).put(matches("https\\:\\/\\/test-webdav\\.local\\/remote-copy-directory\\/\\d{4}-\\d{2}-\\d{2}-\\d{2}\\/local-file\\.jpg"), eq(inputStreamMock));
  }

}
