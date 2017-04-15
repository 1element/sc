package com.github._1element.sc.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.pushover.client.PushoverClient;
import net.pushover.client.PushoverRestClient;

/**
 * Pushover client configuration bean. 
 */
@Configuration
public class PushoverClientConfiguration {

  @Bean
  public PushoverClient pushoverClient() {
    return new PushoverRestClient();
  }
  
}
