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

import java.io.File;
import java.io.FileInputStream;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class FtpRemoteCopyTest {

  @Mock
  private FTPClient ftpClient;

  @Mock
  private FileService fileService;

  @Autowired
  @InjectMocks
  private FtpRemoteCopy ftpRemoteCopy;

  private static final String EXPECTED_FTP_USERNAME = "ftpuser";

  private static final String EXPECTED_FTP_PASSWORD = "secret";

  private static final String EXPECTED_REMOTE_FILENAME = "/remote-copy-directory/local-file.jpg";
  
  private static final String EXPECTED_LOCAL_FILE_PATH = "/tmp/test/local-file.jpg";

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

    File fileMock = mock(File.class);
    Mockito.when(fileMock.getName()).thenReturn("local-file.jpg");
    Mockito.when(fileService.createFile(EXPECTED_LOCAL_FILE_PATH)).thenReturn(fileMock);

    FileInputStream fileInputStreamMock = mock(FileInputStream.class);
    Mockito.when(fileService.createInputStream(any(File.class))).thenReturn(fileInputStreamMock);

    // execute and verify
    RemoteCopyEvent remoteCopyEvent = new RemoteCopyEvent(EXPECTED_LOCAL_FILE_PATH);
    ftpRemoteCopy.handle(remoteCopyEvent);

    verify(ftpClient).login(eq(EXPECTED_FTP_USERNAME), eq(EXPECTED_FTP_PASSWORD));
    verify(ftpClient).storeFile(eq(EXPECTED_REMOTE_FILENAME), any());
  }

}
