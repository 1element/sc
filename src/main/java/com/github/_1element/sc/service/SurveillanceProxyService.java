package com.github._1element.sc.service; //NOSONAR

import com.github._1element.sc.exception.ProxyException;
import com.github._1element.sc.utils.RestTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Simple proxy service class.
 */
@Service
public class SurveillanceProxyService {

  private final RestTemplateBuilder restTemplateBuilder;

  private static final Logger LOG = LoggerFactory.getLogger(SurveillanceProxyService.class);

  @Autowired
  public SurveillanceProxyService(final RestTemplateBuilder restTemplateBuilder) {
    this.restTemplateBuilder = restTemplateBuilder;
  }

  /**
   * Retrieve the provided url (snapshot).
   *
   * @param url the url to retrieve
   * @return the image response
   */
  public ResponseEntity<byte[]> retrieveImage(final String url) throws ProxyException {
    final RestTemplate restTemplate = RestTemplateUtils.buildWithAuth(restTemplateBuilder, url);

    ResponseEntity<byte[]> response = null;
    try {
      response = restTemplate.getForEntity(url, byte[].class);
    } catch (final RestClientException exception) {
      LOG.debug("Could not retrieve snapshot for '{}': '{}'", url, exception.getMessage());
      throw new ProxyException(exception);
    }

    return response;
  }

}
