package com.github._1element.sc.properties; //NOSONAR

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * MQTT specific configuration properties.
 */
@Component
@ConfigurationProperties("sc.mqtt")
public class MqttProperties {

  private boolean enabled = false;

  private String brokerConnection;

  private String topicFilter;

  private String username;

  private String password;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public String getBrokerConnection() {
    return brokerConnection;
  }

  public void setBrokerConnection(final String brokerConnection) {
    this.brokerConnection = brokerConnection;
  }

  public String getTopicFilter() {
    return topicFilter;
  }

  public void setTopicFilter(final String topicFilter) {
    this.topicFilter = topicFilter;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(final String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(final String password) {
    this.password = password;
  }

}
