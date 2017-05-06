package com.github._1element.sc.domain.remotecopy;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.events.RemoteCopyEvent;
import org.apache.commons.net.ftp.FTPClient;
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

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
@PowerMockIgnore({"javax.management.*"})
@PrepareForTest(FtpRemoteCopy.class)
public class FtpRemoteCopyTest {

  @Mock
  private FTPClient ftpClient;

  @Autowired
  @InjectMocks
  private FtpRemoteCopy ftpRemoteCopy;

  private static final String EXPECTED_FTP_USERNAME = "ftpuser";

  private static final String EXPECTED_FTP_PASSWORD = "secret";

  private static final String EXPECTED_REMOTE_FILENAME = "/remote-copy-directory/local-file.jpg";

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
    FileInputStream fileInputStreamMock = mock(FileInputStream.class);

    PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(fileMock);
    PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStreamMock);

    // execute and verify
    RemoteCopyEvent remoteCopyEvent = new RemoteCopyEvent("/tmp/test/local-file.jpg");
    ftpRemoteCopy.handle(remoteCopyEvent);

    verify(ftpClient).login(eq(EXPECTED_FTP_USERNAME), eq(EXPECTED_FTP_PASSWORD));
    verify(ftpClient).storeFile(eq(EXPECTED_REMOTE_FILENAME), any());
  }

}
