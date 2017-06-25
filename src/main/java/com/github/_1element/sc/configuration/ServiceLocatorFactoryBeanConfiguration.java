package com.github._1element.sc.configuration; //NOSONAR

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github._1element.sc.domain.pushnotification.PushNotificationClientFactory;

/**
 * Service locator for factory beans.
 */
@Configuration
public class ServiceLocatorFactoryBeanConfiguration {

  @Bean
  public FactoryBean serviceLocatorFactoryBean() {
    ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
    factoryBean.setServiceLocatorInterface(PushNotificationClientFactory.class);

    return factoryBean;
  }

}
