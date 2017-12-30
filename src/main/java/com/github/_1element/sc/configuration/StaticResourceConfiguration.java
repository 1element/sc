package com.github._1element.sc.configuration; //NOSONAR

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.github._1element.sc.properties.ImageProperties;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Configuration for static resources (images and client).
 */
@Configuration
public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

  public static final String IMAGES_PATH = "/images/";

  private ImageProperties imageProperties;

  @Autowired
  public StaticResourceConfiguration(ImageProperties imageProperties) {
    this.imageProperties = imageProperties;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // expose images directory as static resource
    registry.addResourceHandler(IMAGES_PATH + "**").addResourceLocations("file:" + imageProperties.getStorageDir());

    // map static path to single page app static assets
    registry.addResourceHandler("/static/**").addResourceLocations("classpath:/public/static/");

    // map all other paths to the single page app entry point (index.html)
    registry.addResourceHandler("/**").addResourceLocations("classpath:/public/index.html").resourceChain(true)
        .addResolver(new PathResourceResolver() {
            @Override
            protected Resource getResource(String resourcePath, Resource location) throws IOException {
              return location.exists() && location.isReadable() ? location : null;
            }
        });

  }

}
