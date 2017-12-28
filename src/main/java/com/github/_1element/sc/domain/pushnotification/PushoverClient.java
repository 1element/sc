package com.github._1element.sc.domain.pushnotification; //NOSONAR

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.github._1element.sc.exception.PushNotificationClientException;
import com.github._1element.sc.properties.PushNotificationProperties;

/**
 * Pushover client adapter.
 * This will send push notifications using the pushover.net API.
 */
@Component("pushover")
public class PushoverClient implements PushNotificationClient {

  private final RestTemplate restTemplate;

  private PushNotificationProperties properties;

  private static final String ENDPOINT = "https://api.pushover.net/1/messages.json";

  private static final String PARAM_API_TOKEN = "token";

  private static final String PARAM_USER_TOKEN = "user";

  private static final String PARAM_TITLE = "title";

  private static final String PARAM_MESSAGE = "message";

  private static final String PARAM_URL = "url";

  private static final Logger LOG = LoggerFactory.getLogger(PushoverClient.class);

  @Autowired
  public PushoverClient(RestTemplateBuilder restTemplateBuilder, PushNotificationProperties properties) {
    this.restTemplate = restTemplateBuilder.build();
    this.properties = properties;
  }

  @Override
  public void sendMessage(String title, String text) throws PushNotificationClientException {
    // pushover does not support receiving json
    // so use a MultiValueMap that will be converted to application/x-www-form-urlencoded
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(PARAM_API_TOKEN, properties.getApiToken());
    requestParams.add(PARAM_USER_TOKEN, properties.getUserToken());
    requestParams.add(PARAM_TITLE, title);
    requestParams.add(PARAM_MESSAGE, text);

    try {
      restTemplate.postForObject(ENDPOINT, requestParams, Void.class);
    } catch (RestClientException exception) {
      LOG.error("Error while sending push notification: {}", exception.getMessage());
      throw new PushNotificationClientException(exception);
    }
  }

}
