package com.github._1element.sc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

@EntityScan(basePackageClasses = {SurveillanceCenterApplication.class, Jsr310JpaConverters.class})
@SpringBootApplication
@EnableScheduling
public class SurveillanceCenterApplication {

  @Bean
  public Java8TimeDialect java8TimeDialect() {
    return new Java8TimeDialect();
  }

  public static void main(String[] args) {
    SpringApplication.run(SurveillanceCenterApplication.class, args);
  }

}
