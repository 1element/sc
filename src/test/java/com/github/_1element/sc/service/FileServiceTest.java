package com.github._1element.sc.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class FileServiceTest {

  FileService fileService;

  @Before
  public void setUp() throws Exception {
    fileService = new FileService();
  }

  @Test
  public void testHasValidExtension() throws Exception {
    // two extensions
    String[] validExtensions = {".jpg", ".jpeg"};
    ReflectionTestUtils.setField(fileService, "validExtensions", validExtensions);

    assertTrue(fileService.hasValidExtension("test-file.jpg"));
    assertTrue(fileService.hasValidExtension("another-test-file.jpeg"));
    assertFalse(fileService.hasValidExtension("file-without-extension"));
    assertFalse(fileService.hasValidExtension("file-with-wrong-extenions.png"));
    assertFalse(fileService.hasValidExtension(null));

    // just one extension configured
    String[] validSingleExtension = {".jpg"};
    ReflectionTestUtils.setField(fileService, "validExtensions", validSingleExtension);

    assertTrue(fileService.hasValidExtension("okay.jpg"));
    assertFalse(fileService.hasValidExtension("not-okay.jpeg"));

    // no extension configured
    ReflectionTestUtils.setField(fileService, "validExtensions", null);

    assertTrue(fileService.hasValidExtension("something.jpg"));
    assertTrue(fileService.hasValidExtension("something-completely-different.txt"));
  }

}
