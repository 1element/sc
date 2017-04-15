package com.github._1element.sc.exception; //NOSONAR

public class CameraNotFoundException extends Exception {

  private static final String MESSAGE_CAMERA_NOT_FOUND = "Camera not found.";

  public CameraNotFoundException() {
    super(MESSAGE_CAMERA_NOT_FOUND);
  }

}
