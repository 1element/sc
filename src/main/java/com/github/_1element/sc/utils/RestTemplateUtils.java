package com.github._1element.sc.utils; //NOSONAR

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

public final class RestTemplateUtils {

  private static final Logger LOG = LoggerFactory.getLogger(RestTemplateUtils.class);

  private RestTemplateUtils() {
    // hide constructor for static utility class
  }

  /**
   * Builds a RestTemplate with authorization for the provided URL using the given RestTemplateBuilder.
   * Authorization credentials will be extracted from the URL (http://username:password@host.example/).
   *
   * @param restTemplateBuilder the rest template builder to use
   * @param url the url to use
   * @return rest template
   */
  public static RestTemplate buildWithAuth(RestTemplateBuilder restTemplateBuilder, String url) {
    UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).build();
    String userInfo = uriComponents.getUserInfo();
    if (userInfo != null) {
      List<String> auth = Splitter.on(":").splitToList(userInfo);
      if (auth.size() == 2) {
        return restTemplateBuilder.basicAuthorization(auth.get(0), auth.get(1)).build();
      } else {
        LOG.warn("Could not extract username and password: '{}'", userInfo);
      }
    }

    return restTemplateBuilder.build();
  }

}
