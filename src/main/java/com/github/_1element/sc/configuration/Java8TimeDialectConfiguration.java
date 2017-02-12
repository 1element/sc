package com.github._1element.sc.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

/**
 * Configuration class to support Java 8 time dialect.
 */
@Configuration
public class Java8TimeDialectConfiguration {

  @Bean
  public Java8TimeDialect java8TimeDialect() {
    return new Java8TimeDialect();
  }

}
