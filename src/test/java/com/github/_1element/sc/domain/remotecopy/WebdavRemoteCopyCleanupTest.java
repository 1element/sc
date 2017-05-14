package com.github._1element.sc.domain.remotecopy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.properties.WebdavRemoteCopyProperties;
import com.github.sardine.Sardine;
import com.github.sardine.DavResource;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
@PowerMockIgnore({"javax.management.*", "org.apache.http.conn.ssl.*"})
@PrepareForTest(WebdavRemoteCopyCleanup.class)
public class WebdavRemoteCopyCleanupTest {

  @Mock
  private Sardine sardine;

  @Autowired
  @InjectMocks
  private WebdavRemoteCopyCleanup webdavRemoteCopyCleanup;
  
  private static final String LOCATION = "https://test-webdav.local/remote-copy-directory/";

  @Test
  public void testCleanupByAge() throws Exception {
    // test fixtures
    DavResource davResource1 = mock(DavResource.class);
    Mockito.when(davResource1.isDirectory()).thenReturn(false);
    Mockito.when(davResource1.getName()).thenReturn("3mb-testfile.jpg");
    Mockito.when(davResource1.getContentLength()).thenReturn(3145728L);
    Mockito.when(davResource1.getCreation()).thenReturn(new Date());

    DavResource davResource2 = mock(DavResource.class);
    Mockito.when(davResource1.isDirectory()).thenReturn(false);
    Mockito.when(davResource1.getName()).thenReturn("2mb-very-old-testfile.jpg");
    Mockito.when(davResource1.getContentLength()).thenReturn(2097152L);
    Date oldDate = new Date(1483225200000L);
    Mockito.when(davResource1.getCreation()).thenReturn(oldDate);

    List<DavResource> davResources = new ArrayList<>();
    davResources.add(davResource1);
    davResources.add(davResource2);

    // mock behaviour
    Mockito.when(sardine.list(LOCATION)).thenReturn(davResources);

    // execute and verify
    webdavRemoteCopyCleanup.cleanup();

    verify(sardine).delete(LOCATION + "2mb-very-old-testfile.jpg");
  }

  @Test
  public void testCleanupByQuota() throws Exception {
    // test fixtures
    DavResource davResource1 = mock(DavResource.class);
    Mockito.when(davResource1.isDirectory()).thenReturn(false);
    Mockito.when(davResource1.getName()).thenReturn("5mb-testfile.jpg");
    Mockito.when(davResource1.getContentLength()).thenReturn(5242880L);
    Mockito.when(davResource1.getCreation()).thenReturn(new Date());

    DavResource davResource2 = mock(DavResource.class);
    Mockito.when(davResource1.isDirectory()).thenReturn(false);
    Mockito.when(davResource1.getName()).thenReturn("6mb-testfile.jpg");
    Mockito.when(davResource1.getContentLength()).thenReturn(6291456L);
    Date oldDate = new Date(1483225200000L);
    Mockito.when(davResource1.getCreation()).thenReturn(oldDate);

    List<DavResource> davResources = new ArrayList<>();
    davResources.add(davResource1);
    davResources.add(davResource2);

    // mock behaviour
    Mockito.when(sardine.list(LOCATION)).thenReturn(davResources);

    // execute and verify
    webdavRemoteCopyCleanup.cleanup();

    verify(sardine).delete(LOCATION + "6mb-testfile.jpg");
  }

  @Test
  @DirtiesContext
  public void testCleanupDisabled() throws Exception {
    WebdavRemoteCopyProperties webdavRemoteCopyProperties = mock(WebdavRemoteCopyProperties.class);
    Mockito.when(webdavRemoteCopyProperties.isEnabled()).thenReturn(false);
    ReflectionTestUtils.setField(webdavRemoteCopyCleanup, "webdavRemoteCopyProperties", webdavRemoteCopyProperties);

    webdavRemoteCopyCleanup.cleanup();

    verifyZeroInteractions(sardine);
  }
  
}
