package com.github._1element.sc.domain.remotecopy;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.events.RemoteCopyEvent;
import com.github.sardine.Sardine;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
@PowerMockIgnore({"javax.management.*", "org.apache.http.conn.ssl.*"})
@PrepareForTest(WebdavRemoteCopy.class)
public class WebdavRemoteCopyTest {

  @Mock
  private Sardine sardine;

  @Autowired
  @InjectMocks
  private WebdavRemoteCopy webdavRemoteCopy;

  @Test
  public void testHandle() throws Exception {
    // mocking
    MockitoAnnotations.initMocks(this);
    File fileMock = mock(File.class);
    Mockito.when(fileMock.getName()).thenReturn("local-file.jpg");
    FileInputStream fileInputStreamMock = mock(FileInputStream.class);

    PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(fileMock);
    PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(fileInputStreamMock);

    // execute and verify
    RemoteCopyEvent remoteCopyEvent = new RemoteCopyEvent("/tmp/test/local-file.jpg");
    webdavRemoteCopy.handle(remoteCopyEvent);

    verify(sardine).put(eq("https://test-webdav.local/remote-copy-directory/local-file.jpg"), eq(fileInputStreamMock));
  }

}
