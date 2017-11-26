package com.github._1element.sc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.github._1element.sc.properties.ImageProperties;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class FileServiceTest {

  @Test
  public void testHasValidExtensionWithTwoExtensions() throws Exception {
    // two extensions
    ImageProperties imageProperties = new ImageProperties();
    imageProperties.setValidExtensions(new String[]{".jpg", ".jpeg"});

    FileService fileService = new FileService(imageProperties);

    assertTrue(fileService.hasValidExtension("test-file.jpg"));
    assertTrue(fileService.hasValidExtension("another-test-file.jpeg"));
    assertFalse(fileService.hasValidExtension("file-without-extension"));
    assertFalse(fileService.hasValidExtension("file-with-wrong-extenions.png"));
    assertFalse(fileService.hasValidExtension(null));
  }

  @Test
  public void testHasValidExtensionWithOneExtension() throws Exception {
    // just one extension configured
    ImageProperties imageProperties = new ImageProperties();
    imageProperties.setValidExtensions(new String[]{".jpg"});

    FileService fileService = new FileService(imageProperties);

    assertTrue(fileService.hasValidExtension("okay.jpg"));
    assertFalse(fileService.hasValidExtension("not-okay.jpeg"));
  }

  @Test
  public void testHasValidExtensionWithNoConfiguration() throws Exception {
    // no extension configured
    ImageProperties imageProperties = new ImageProperties();
    imageProperties.setValidExtensions(null);

    FileService fileService = new FileService(imageProperties);

    assertTrue(fileService.hasValidExtension("something.jpg"));
    assertTrue(fileService.hasValidExtension("something-completely-different.txt"));
  }

  @Test
  public void testGetUniquePrefix() throws Exception {
    FileService fileService = new FileService(null);

    String result1 = fileService.getUniquePrefix();
    String result2 = fileService.getUniquePrefix();

    assertNotNull(result1);
    assertNotNull(result2);
    assertTrue(result1.matches("\\d{4}-\\d{2}-\\d{2}-\\d{2}-\\d{2}-\\d{2}-\\d{3}-(.){7}"));
    assertNotEquals(result1, result2);
  }

}
