package com.github._1element.sc.domain;

import com.github._1element.sc.repository.CameraRepository;
import com.github._1element.sc.service.FileService;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UploadFtpletTest {

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private CameraRepository cameraRepository;

  @Mock
  private FileService fileService;

  @Mock
  private FtpSession ftpSession;

  @Mock
  private FtpRequest ftpRequest;

  @InjectMocks
  private UploadFtplet uploadFtplet;

  @Before
  public void setUp() throws Exception {
    uploadFtplet = new UploadFtplet(eventPublisher, cameraRepository, fileService);
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testOnUploadStartForInvalidFileExtension() throws Exception {
    // arrange
    when(fileService.hasValidExtension(any())).thenReturn(false);

    // act
    FtpletResult result = uploadFtplet.onUploadStart(ftpSession, ftpRequest);

    // assert
    assertEquals(FtpletResult.SKIP, result);
  }

  @Test
  public void testOnUploadStartForValidFileExtension() throws Exception {
    // arrange
    when(fileService.hasValidExtension(any())).thenReturn(true);

    // act
    FtpletResult result = uploadFtplet.onUploadStart(ftpSession, ftpRequest);

    // assert
    assertEquals(FtpletResult.DEFAULT, result);
  }

  @Test
  public void onUploadEnd() throws Exception {
    // arrange
    User user = mock(User.class);
    when(user.getHomeDirectory()).thenReturn("user-root/");
    when(user.getName()).thenReturn("user-name");
    when(ftpSession.getUser()).thenReturn(user);

    FtpFile ftpFile = mock(FtpFile.class);
    when(ftpFile.getAbsolutePath()).thenReturn("absolute-path/");
    FileSystemView fileSystemView = mock(FileSystemView.class);
    when(fileSystemView.getWorkingDirectory()).thenReturn(ftpFile);
    when(ftpSession.getFileSystemView()).thenReturn(fileSystemView);
    when(ftpRequest.getArgument()).thenReturn("file-argument.jpg");

    Camera camera = mock(Camera.class);
    when(cameraRepository.findByFtpUsername("user-name")).thenReturn(camera);

    // act
    uploadFtplet.onUploadEnd(ftpSession, ftpRequest);

    // assert
    verify(fileService).delete(Paths.get("user-root/absolute-path/file-argument.jpg"));
  }

  @Test
  public void onDeleteStart() throws Exception {
    // act
    FtpletResult result = uploadFtplet.onDeleteStart(ftpSession, ftpRequest);

    // assert
    assertEquals(FtpletResult.SKIP, result);
  }

  @Test
  public void onDownloadStart() throws Exception {
    // act
    FtpletResult result = uploadFtplet.onDownloadStart(ftpSession, ftpRequest);

    // assert
    assertEquals(FtpletResult.SKIP, result);
  }

}
