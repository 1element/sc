package com.github._1element.sc.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuration to expose images directory as static resource.
 */
@Configuration
public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

  @Value("${sc.image.storage-dir}")
  private String imagesDirectory;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**").addResourceLocations("file:" + imagesDirectory);
  }

}
