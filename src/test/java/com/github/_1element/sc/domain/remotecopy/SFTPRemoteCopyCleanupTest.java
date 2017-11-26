package com.github._1element.sc.domain.remotecopy;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.properties.SFTPRemoteCopyProperties;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class SFTPRemoteCopyCleanupTest {

  @Autowired
  private SFTPRemoteCopyProperties sftpRemoteCopyProperties;

  @Mock
  private JSch jsch;

  @Mock
  private ChannelSftp sftpChannel;

  @Autowired
  @InjectMocks
  private SFTPRemoteCopyCleanup sftpRemoteCopyCleanup;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    Session session = mock(Session.class);

    Mockito.when(session.openChannel(any())).thenReturn(sftpChannel);
    Mockito.when(jsch.getSession(sftpRemoteCopyProperties.getUsername(), sftpRemoteCopyProperties.getHost())).thenReturn(session);
  }

  @Test
  public void testCleanupByAge() throws Exception {
    // test content
    ChannelSftp.LsEntry file1 = mock(ChannelSftp.LsEntry.class);
    SftpATTRS file1Attributes = mock(SftpATTRS.class);
    Mockito.when(file1Attributes.isDir()).thenReturn(false);
    Mockito.when(file1Attributes.getSize()).thenReturn((long) 3145728);
    Mockito.when(file1Attributes.getMTime()).thenReturn((int) LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    Mockito.when(file1.getAttrs()).thenReturn(file1Attributes);
    Mockito.when(file1.getFilename()).thenReturn("3mb-testfile.jpg");

    ChannelSftp.LsEntry file2 = mock(ChannelSftp.LsEntry.class);
    SftpATTRS file2Attributes = mock(SftpATTRS.class);
    Mockito.when(file2Attributes.isDir()).thenReturn(false);
    Mockito.when(file2Attributes.getSize()).thenReturn((long) 2097152);
    Mockito.when(file2Attributes.getMTime()).thenReturn((int) LocalDateTime.now().minusDays(10).toEpochSecond(ZoneOffset.UTC));
    Mockito.when(file2.getAttrs()).thenReturn(file2Attributes);
    Mockito.when(file2.getFilename()).thenReturn("2mb-very-old-testfile.jpg");

    Vector<ChannelSftp.LsEntry> fileList = new Vector<>();
    fileList.add(file1);
    fileList.add(file2);

    Mockito.when(sftpChannel.ls(sftpRemoteCopyProperties.getDir())).thenReturn(fileList);

    // execute and verify
    sftpRemoteCopyCleanup.cleanup();
    verify(sftpChannel).rm(sftpRemoteCopyProperties.getDir() + "2mb-very-old-testfile.jpg");
  }

  @Test
  public void testCleanupByQuota() throws Exception {
    // test content
    ChannelSftp.LsEntry file1 = mock(ChannelSftp.LsEntry.class);
    SftpATTRS file1Attributes = mock(SftpATTRS.class);
    Mockito.when(file1Attributes.isDir()).thenReturn(false);
    Mockito.when(file1Attributes.getSize()).thenReturn((long) 5242880);
    Mockito.when(file1Attributes.getMTime()).thenReturn((int) LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
    Mockito.when(file1.getAttrs()).thenReturn(file1Attributes);
    Mockito.when(file1.getFilename()).thenReturn("5mb-testfile.jpg");

    ChannelSftp.LsEntry file2 = mock(ChannelSftp.LsEntry.class);
    SftpATTRS file2Attributes = mock(SftpATTRS.class);
    Mockito.when(file2Attributes.isDir()).thenReturn(false);
    Mockito.when(file2Attributes.getSize()).thenReturn((long) 6291456);
    Mockito.when(file2Attributes.getMTime()).thenReturn((int) LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.UTC));
    Mockito.when(file2.getAttrs()).thenReturn(file2Attributes);
    Mockito.when(file2.getFilename()).thenReturn("6mb-testfile.jpg");

    Vector<ChannelSftp.LsEntry> fileList = new Vector<>();
    fileList.add(file1);
    fileList.add(file2);

    Mockito.when(sftpChannel.ls(sftpRemoteCopyProperties.getDir())).thenReturn(fileList);

    // execute and verify
    sftpRemoteCopyCleanup.cleanup();
    verify(sftpChannel).rm(sftpRemoteCopyProperties.getDir() + "6mb-testfile.jpg");
  }

  @Test
  @DirtiesContext
  public void testCleanupDisabled() throws Exception {
    SFTPRemoteCopyProperties sftpRemoteCopyProperties = mock(SFTPRemoteCopyProperties.class);
    Mockito.when(sftpRemoteCopyProperties.isEnabled()).thenReturn(false);
    ReflectionTestUtils.setField(sftpRemoteCopyCleanup, "sftpRemoteCopyProperties", sftpRemoteCopyProperties);

    sftpRemoteCopyCleanup.cleanup();

    verifyZeroInteractions(jsch);
  }

}
