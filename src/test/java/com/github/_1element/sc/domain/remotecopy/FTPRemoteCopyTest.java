package com.github._1element.sc.domain.remotecopy;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.service.FileService;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class FTPRemoteCopyTest {

  @Mock
  private FTPClient ftpClient;

  @Mock
  private FileService fileService;

  @Autowired
  @InjectMocks
  private FTPRemoteCopy ftpRemoteCopy;

  private static final String EXPECTED_FTP_USERNAME = "ftpuser";

  private static final String EXPECTED_FTP_PASSWORD = "secret";

  private static final String EXPECTED_REMOTE_FILENAME = "/remote-copy-directory/local-file.jpg";

  private static final String EXPECTED_LOCAL_FILE_PATH = "/tmp/test/local-file.jpg";

  /**
   * Setup for all tests.
   *
   * @throws Exception exception in case of an error.
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Mockito.when(ftpClient.getReplyCode()).thenReturn(200);
    Mockito.when(ftpClient.login(eq(EXPECTED_FTP_USERNAME), eq(EXPECTED_FTP_PASSWORD))).thenReturn(true);
  }

  @Test
  public void testHandle() throws Exception {
    // mocking
    Mockito.when(ftpClient.storeFile(eq(EXPECTED_REMOTE_FILENAME), any())).thenReturn(true);

    Path pathMock = mock(Path.class);
    Mockito.when(pathMock.getFileName()).thenReturn(Paths.get("local-file.jpg"));
    Mockito.when(fileService.getPath(EXPECTED_LOCAL_FILE_PATH)).thenReturn(pathMock);

    InputStream inputStreamMock = mock(InputStream.class);
    Mockito.when(fileService.createInputStream(any(Path.class))).thenReturn(inputStreamMock);

    // execute and verify
    RemoteCopyEvent remoteCopyEvent = new RemoteCopyEvent(EXPECTED_LOCAL_FILE_PATH);
    ftpRemoteCopy.handle(remoteCopyEvent);

    verify(ftpClient).login(eq(EXPECTED_FTP_USERNAME), eq(EXPECTED_FTP_PASSWORD));
    verify(ftpClient).storeFile(eq(EXPECTED_REMOTE_FILENAME), any());
  }

}
