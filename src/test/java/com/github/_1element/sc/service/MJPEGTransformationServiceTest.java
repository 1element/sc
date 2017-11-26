package com.github._1element.sc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.boot.web.client.RestTemplateBuilder;

import com.github._1element.sc.properties.MJPEGTransformProperties;

@RunWith(JUnit4.class)
public class MJPEGTransformationServiceTest {

  private MJPEGTransformationService mjpegTransformationService;

  /**
   * Setup for all tests.
   *
   * @throws Exception exception in case of an error
   */
  @Before
  public void setUp() throws Exception {
    MJPEGTransformProperties properties = new MJPEGTransformProperties();
    String[] urls = { "http://camera1.local/cgi-bin/CGIProxy.fcgi", "http://camera2.local/snapshot.cgi" };
    properties.setUrls(urls);

    mjpegTransformationService = new MJPEGTransformationService(properties, new RestTemplateBuilder());
  }

  @Test
  public void testGetSnapshotUrl() throws Exception {
    assertNull(mjpegTransformationService.getSnapshotUrl(0));
    assertEquals("http://camera1.local/cgi-bin/CGIProxy.fcgi", mjpegTransformationService.getSnapshotUrl(1));
    assertEquals("http://camera2.local/snapshot.cgi", mjpegTransformationService.getSnapshotUrl(2));
    assertNull(mjpegTransformationService.getSnapshotUrl(3));
  }

}
