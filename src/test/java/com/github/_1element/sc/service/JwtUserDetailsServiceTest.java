package com.github._1element.sc.service;

import com.github._1element.sc.SurveillanceCenterApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SurveillanceCenterApplication.class)
@WebAppConfiguration
public class JwtUserDetailsServiceTest {

  @Autowired
  private JwtUserDetailsService jwtUserDetailsService;

  @Test
  public void testLoadUserByUsername() throws Exception {
    UserDetails userDetailsResult = jwtUserDetailsService.loadUserByUsername("admin");

    assertEquals("admin", userDetailsResult.getUsername());
    assertEquals("$2a$04$xdRJiiGwwHEbSgs6ucM0DOOCVEUQVaKtB3UPO16.h65sCWzPlkFHC", userDetailsResult.getPassword());
    assertEquals("[ROLE_USER]", userDetailsResult.getAuthorities().toString());
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testLoadUserByUsernameNotFound() throws Exception {
    jwtUserDetailsService.loadUserByUsername("invalid-username");
  }

  @Test(expected = UsernameNotFoundException.class)
  public void testLoadUserByUsernameEmpty() throws Exception {
    jwtUserDetailsService.loadUserByUsername("");
  }

  @Test(expected = NullPointerException.class)
  public void testLoadUserByUsernameNull() throws Exception {
    jwtUserDetailsService.loadUserByUsername(null);
  }

}
