package com.github._1element.sc.configuration; //NOSONAR

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github._1element.sc.properties.ImageProperties;

/**
 * Configuration to expose images directory as static resource.
 */
@Configuration
public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

  private ImageProperties imageProperties;

  @Autowired
  public StaticResourceConfiguration(ImageProperties imageProperties) {
    this.imageProperties = imageProperties;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/**").addResourceLocations("file:" + imageProperties.getStorageDir());
  }

}
