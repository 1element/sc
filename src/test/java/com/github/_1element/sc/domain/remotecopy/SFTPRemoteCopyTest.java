package com.github._1element.sc.domain.remotecopy;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
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
import com.github._1element.sc.properties.SFTPRemoteCopyProperties;
import com.github._1element.sc.service.FileService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class SFTPRemoteCopyTest {

  @Autowired
  private SFTPRemoteCopyProperties sftpRemoteCopyProperties;

  @Mock
  private JSch jsch;

  @Mock
  private FileService fileService;

  @Autowired
  @InjectMocks
  private SFTPRemoteCopy sftpRemoteCopy;

  private static final String EXPECTED_LOCAL_FILE_PATH = "/tmp/test/local-file.jpg";

  private static final String EXPECTED_FILE_NAME = "local-file.jpg";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testHandle() throws Exception {
    // mocking
    Session sessionMock = mock(Session.class);
    ChannelSftp channelMock = mock(ChannelSftp.class);

    Mockito.when(sessionMock.openChannel(any())).thenReturn(channelMock);
    Mockito.when(jsch.getSession(sftpRemoteCopyProperties.getUsername(), sftpRemoteCopyProperties.getHost())).thenReturn(sessionMock);

    Path pathMock = mock(Path.class);
    Mockito.when(pathMock.getFileName()).thenReturn(Paths.get(EXPECTED_FILE_NAME));
    Mockito.when(fileService.getPath(EXPECTED_LOCAL_FILE_PATH)).thenReturn(pathMock);

    InputStream inputStreamMock = mock(InputStream.class);
    Mockito.when(fileService.createInputStream(any(Path.class))).thenReturn(inputStreamMock);

    // execute
    RemoteCopyEvent remoteCopyEvent = new RemoteCopyEvent(EXPECTED_LOCAL_FILE_PATH);
    sftpRemoteCopy.handle(remoteCopyEvent);

    // verify
    verify(channelMock).cd(sftpRemoteCopyProperties.getDir());
    verify(channelMock).put(inputStreamMock, EXPECTED_FILE_NAME);
  }

}
