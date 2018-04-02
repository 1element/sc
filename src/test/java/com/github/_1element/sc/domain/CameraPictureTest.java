package com.github._1element.sc.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;

@RunWith(JUnit4.class)
public class CameraPictureTest {

  @Test(expected = IllegalArgumentException.class)
  public void testEmptySnapshotUrl() throws Exception {
    new CameraPicture("", true, true);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullSnapshotUrl() throws Exception {
    new CameraPicture(null, true, true);
  }

  @Test
  public void testDisabledSnapshotAndStream() throws Exception {
    final CameraPicture cameraPicture = new CameraPicture("", false, false);

    assertFalse(cameraPicture.isSnapshotEnabled());
    assertFalse(cameraPicture.isStreamEnabled());
  }

}
