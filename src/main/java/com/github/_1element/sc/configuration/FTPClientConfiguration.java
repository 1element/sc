package com.github._1element.sc.configuration;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * FTP client configuration bean.
 */
@Configuration
public class FTPClientConfiguration {

  @Bean
  @Scope("prototype")
  public FTPClient ftpClient() {
    return new FTPClient();
  }

}
