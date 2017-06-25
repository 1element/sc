package com.github._1element.sc.exception; //NOSONAR

public class PushNotificationClientException extends Exception {

  private static final String MESSAGE = "Push notification could not be delivered to external service.";

  public PushNotificationClientException(Throwable cause) {
    super(MESSAGE, cause);
  }

}
