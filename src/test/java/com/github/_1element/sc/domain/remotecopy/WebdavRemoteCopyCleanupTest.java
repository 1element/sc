package com.github._1element.sc.domain.remotecopy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class WebdavRemoteCopyCleanupTest {

  @Mock
  private Sardine sardine;

  @Autowired
  @InjectMocks
  private WebdavRemoteCopyCleanup webdavRemoteCopyCleanup;

  private static final String BASE_LOCATION = "https://test-webdav.local/remote-copy-directory/";

  @Test
  public void testCleanupByAge() throws Exception {
    // test fixtures
    DavResource davDirectory1 = mock(DavResource.class);
    Mockito.when(davDirectory1.isDirectory()).thenReturn(true);
    Mockito.when(davDirectory1.getName()).thenReturn("2017-06-15");

    DavResource davResource1 = mock(DavResource.class);
    Mockito.when(davResource1.isDirectory()).thenReturn(false);
    Mockito.when(davResource1.getName()).thenReturn("3mb-testfile.jpg");
    Mockito.when(davResource1.getHref()).thenReturn(new URI("/remote-copy-directory/2017-06-15/3mb-testfile.jpg"));
    Mockito.when(davResource1.getContentLength()).thenReturn(3145728L);
    Mockito.when(davResource1.getCreation()).thenReturn(new Date());

    DavResource davResource2 = mock(DavResource.class);
    Mockito.when(davResource1.isDirectory()).thenReturn(false);
    Mockito.when(davResource1.getName()).thenReturn("2mb-very-old-testfile.jpg");
    Mockito.when(davResource1.getHref()).thenReturn(
        new URI("/remote-copy-directory/2017-06-15/2mb-very-old-testfile.jpg"));
    Mockito.when(davResource1.getContentLength()).thenReturn(2097152L);
    Date oldDate = new Date(1483225200000L);
    Mockito.when(davResource1.getCreation()).thenReturn(oldDate);

    List<DavResource> davRootResources = Arrays.asList(davDirectory1);
    List<DavResource> davSubResources = Arrays.asList(davResource1, davResource2);

    // mock behaviour
    Mockito.when(sardine.list(BASE_LOCATION)).thenReturn(davRootResources);
    Mockito.when(sardine.list(BASE_LOCATION + "2017-06-15/")).thenReturn(davSubResources);

    // execute and verify
    webdavRemoteCopyCleanup.cleanup();

    verify(sardine).delete(BASE_LOCATION + "2017-06-15/2mb-very-old-testfile.jpg");
  }

  @Test
  public void testCleanupByQuota() throws Exception {
    // test fixtures
    DavResource davDirectory1 = mock(DavResource.class);
    Mockito.when(davDirectory1.isDirectory()).thenReturn(true);
    Mockito.when(davDirectory1.getName()).thenReturn("2017-06-10");

    DavResource davResource1 = mock(DavResource.class);
    Mockito.when(davResource1.isDirectory()).thenReturn(false);
    Mockito.when(davResource1.getName()).thenReturn("5mb-testfile.jpg");
    Mockito.when(davResource1.getHref()).thenReturn(new URI("/remote-copy-directory/2017-06-10/5mb-testfile.jpg"));
    Mockito.when(davResource1.getContentLength()).thenReturn(5242880L);
    Mockito.when(davResource1.getCreation()).thenReturn(new Date());

    DavResource davResource2 = mock(DavResource.class);
    Mockito.when(davResource1.isDirectory()).thenReturn(false);
    Mockito.when(davResource1.getName()).thenReturn("6mb-testfile.jpg");
    Mockito.when(davResource1.getHref()).thenReturn(new URI("/remote-copy-directory/2017-06-10/6mb-testfile.jpg"));

    Mockito.when(davResource1.getContentLength()).thenReturn(6291456L);
    Date oldDate = new Date(1483225200000L);
    Mockito.when(davResource1.getCreation()).thenReturn(oldDate);

    List<DavResource> davRootResources = Arrays.asList(davDirectory1);
    List<DavResource> davSubResources = Arrays.asList(davResource1, davResource2);

    // mock behaviour
    Mockito.when(sardine.list(BASE_LOCATION)).thenReturn(davRootResources);
    Mockito.when(sardine.list(BASE_LOCATION + "2017-06-10/")).thenReturn(davSubResources);

    // execute and verify
    webdavRemoteCopyCleanup.cleanup();

    verify(sardine).delete(BASE_LOCATION + "2017-06-10/6mb-testfile.jpg");
  }

  @Test
  public void testCleanupEmptyDirectory() throws Exception {
    DavResource davDirectory = mock(DavResource.class);
    Mockito.when(davDirectory.isDirectory()).thenReturn(true);
    Mockito.when(davDirectory.getName()).thenReturn("2017-06-25");

    DavResource davSubResource = mock(DavResource.class);
    Mockito.when(davSubResource.isDirectory()).thenReturn(true);

    List<DavResource> davRootResources = Arrays.asList(davDirectory);
    List<DavResource> davSubResources = Arrays.asList(davSubResource);

    // mock behaviour
    Mockito.when(sardine.list(BASE_LOCATION)).thenReturn(davRootResources);
    Mockito.when(sardine.list(BASE_LOCATION + "2017-06-25/")).thenReturn(davSubResources);

    // execute and verify
    webdavRemoteCopyCleanup.cleanup();

    verify(sardine).delete(BASE_LOCATION + "2017-06-25/");
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
