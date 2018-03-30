package com.github._1element.sc.exception; //NOSONAR

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CameraNotFoundException extends Exception {

  private static final String MESSAGE_CAMERA_NOT_FOUND = "Camera not found.";

  public CameraNotFoundException() {
    super(MESSAGE_CAMERA_NOT_FOUND);
  }

}
