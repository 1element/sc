package com.github._1element.sc.exception; //NOSONAR

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends Exception {

  public ForbiddenException(String message) {
    super(message);
  }

}
