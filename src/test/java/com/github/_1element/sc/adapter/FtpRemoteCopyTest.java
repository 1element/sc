package com.github._1element.sc.adapter;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.events.RemoteCopyEvent;
import com.github._1element.sc.properties.FtpRemoteCopyProperties;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SurveillanceCenterApplication.class})
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

  @Test
  public void testCleanupByAge() throws Exception {
    // test content
    FTPFile ftpFile1 = mock(FTPFile.class);
    Mockito.when(ftpFile1.isFile()).thenReturn(true);
    Mockito.when(ftpFile1.getName()).thenReturn("3mb-testfile.jpg");
    Mockito.when(ftpFile1.getSize()).thenReturn((long)3145728);
    Mockito.when(ftpFile1.getTimestamp()).thenReturn(Calendar.getInstance());

    FTPFile ftpFile2 = mock(FTPFile.class);
    Mockito.when(ftpFile2.isFile()).thenReturn(true);
    Mockito.when(ftpFile2.getName()).thenReturn("2mb-very-old-testfile.jpg");
    Mockito.when(ftpFile2.getSize()).thenReturn((long)2097152);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, -10);
    Mockito.when(ftpFile2.getTimestamp()).thenReturn(calendar);

    FTPFile[] ftpFiles = {ftpFile1, ftpFile2};

    // mock behaviour
    Mockito.when(ftpClient.changeWorkingDirectory(anyString())).thenReturn(true);
    Mockito.when(ftpClient.deleteFile(anyString())).thenReturn(true);
    Mockito.when(ftpClient.listFiles()).thenReturn(ftpFiles);

    // execute and verify
    ftpRemoteCopy.cleanup();

    verify(ftpClient).login(eq(EXPECTED_FTP_USERNAME), eq(EXPECTED_FTP_PASSWORD));
    verify(ftpClient).deleteFile("2mb-very-old-testfile.jpg");
  }

  @Test
  public void testCleanupByQuota() throws Exception {
    // test content
    FTPFile ftpFile1 = mock(FTPFile.class);
    Mockito.when(ftpFile1.isFile()).thenReturn(true);
    Mockito.when(ftpFile1.getName()).thenReturn("5mb-testfile.jpg");
    Mockito.when(ftpFile1.getSize()).thenReturn((long) 5242880);
    Mockito.when(ftpFile1.getTimestamp()).thenReturn(Calendar.getInstance());

    FTPFile ftpFile2 = mock(FTPFile.class);
    Mockito.when(ftpFile2.isFile()).thenReturn(true);
    Mockito.when(ftpFile2.getName()).thenReturn("6mb-testfile.jpg");
    Mockito.when(ftpFile2.getSize()).thenReturn((long) 6291456);
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, -1);
    Mockito.when(ftpFile2.getTimestamp()).thenReturn(calendar);

    FTPFile[] ftpFiles = {ftpFile1, ftpFile2};

    // mock behaviour
    Mockito.when(ftpClient.changeWorkingDirectory(anyString())).thenReturn(true);
    Mockito.when(ftpClient.deleteFile(anyString())).thenReturn(true);
    Mockito.when(ftpClient.listFiles()).thenReturn(ftpFiles);

    // execute and verify
    ftpRemoteCopy.cleanup();

    verify(ftpClient).login(eq(EXPECTED_FTP_USERNAME), eq(EXPECTED_FTP_PASSWORD));
    verify(ftpClient).deleteFile("6mb-testfile.jpg");
  }

  @Test
  @DirtiesContext
  public void testCleanupDisabled() throws Exception {
    FtpRemoteCopyProperties ftpRemoteCopyProperties = mock(FtpRemoteCopyProperties.class);
    Mockito.when(ftpRemoteCopyProperties.isEnabled()).thenReturn(false);
    ReflectionTestUtils.setField(ftpRemoteCopy, "ftpRemoteCopyProperties", ftpRemoteCopyProperties);

    ftpRemoteCopy.cleanup();

    verifyZeroInteractions(ftpClient);
  }

}
