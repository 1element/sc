package com.github._1element.sc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertTrue;

import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class SurveillanceCenterApplicationTests {

  @Test
  public void testContextLoads() throws Exception {
    Properties properties = PropertiesLoaderUtils.loadAllProperties("application.properties");
    assertTrue(properties.size() > 0);
  }

}
