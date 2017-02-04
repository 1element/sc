package com.github._1element.sc;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Properties;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class SurveillanceCenterApplicationTests {

  @Test
  public void testContextLoads() throws Exception {
    Properties properties = PropertiesLoaderUtils.loadAllProperties("application.properties");
    Assert.assertTrue(properties.size() > 0);
  }

}
