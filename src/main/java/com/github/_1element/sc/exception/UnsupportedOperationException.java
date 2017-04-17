package com.github._1element.sc.exception; //NOSONAR

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnsupportedOperationException extends Exception {

  public UnsupportedOperationException(String message) {
    super(message);
  }
  
}
