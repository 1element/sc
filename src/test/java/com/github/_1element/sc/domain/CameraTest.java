package com.github._1element.sc.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class CameraTest {

  @Test(expected = IllegalArgumentException.class)
  public void testEmptySnapshotUrl() throws Exception {
    new Camera(null, null, null, null, null, null, "", true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSnapshotUrl() throws Exception {
    new Camera(null, null, null, null, null, null, null, true, true);
  }

  @Test
  public void testDisabledSnapshotAndStream() throws Exception {
    Camera camera = new Camera("cameraId", null, null, null, null, null, "", false, false);

    assertEquals("cameraId", camera.getId());
  }

}
