package com.github._1element.sc.configuration; //NOSONAR

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

/**
 * WebDAV client configuration bean.
 */
@Configuration
public class WebdavClientConfiguraton {

  @Bean
  @Scope("prototype")
  public Sardine sardine() {
    return SardineFactory.begin();
  }

}
