package com.github._1element.sc.exception; //NOSONAR

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ProxyException extends Exception {

  public ProxyException(Throwable cause) {
    super(cause);
  }

}
