package com.github._1element.sc; //NOSONAR

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(basePackageClasses = {SurveillanceCenterApplication.class, Jsr310JpaConverters.class})
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SurveillanceCenterApplication {

  public static void main(String[] args) {
    SpringApplication.run(SurveillanceCenterApplication.class, args);
  }

}
