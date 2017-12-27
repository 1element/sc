package com.github._1element.sc.configuration; //NOSONAR

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapper configuration bean.
 * This allows mapping of entities/domain objects to DTOs (REST resources).
 */
@Configuration
public class ModelMapperConfiguration {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

}
