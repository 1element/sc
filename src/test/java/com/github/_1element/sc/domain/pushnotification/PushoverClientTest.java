package com.github._1element.sc.domain.pushnotification;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import com.github._1element.sc.SurveillanceCenterApplication;
import com.github._1element.sc.exception.PushNotificationClientException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class PushoverClientTest {

  @Autowired
  private PushoverClient pushoverClient;

  private MockRestServiceServer mockServer;

  private static final String EXPECTED_ENDPOINT = "https://api.pushover.net/1/messages.json";

  /**
   * Setup for all tests.
   *
   * @throws Exception exception in case of an error
   */
  @Before
  public void setUp() throws Exception {
    RestTemplate restTemplate = new RestTemplate();
    ReflectionTestUtils.setField(pushoverClient, "restTemplate", restTemplate);
    mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void testSendMessage() throws Exception {
    mockServer.expect(requestTo(EXPECTED_ENDPOINT))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("{ \"status\" : 1}", MediaType.APPLICATION_JSON));

    pushoverClient.sendMessage("Title", "Message text");

    mockServer.verify();
  }

  @Test(expected = PushNotificationClientException.class)
  public void testSendMessageErrorResponse() throws Exception {
    mockServer.expect(requestTo(EXPECTED_ENDPOINT))
      .andExpect(method(HttpMethod.POST))
      .andRespond(withBadRequest());

    pushoverClient.sendMessage("Title", "Message text");

    mockServer.verify();
  }

}
