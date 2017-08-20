package com.github._1element.sc.configuration; //NOSONAR

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.jcraft.jsch.JSch;

/**
 * SFTP client configuration bean.
 */
@Configuration
public class SFTPClientConfiguration {

  @Bean
  @Scope("prototype")
  public JSch jsch() {
    return new JSch();
  }

}
