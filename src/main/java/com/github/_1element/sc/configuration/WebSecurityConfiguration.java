package com.github._1element.sc.configuration; //NOSONAR

import com.github._1element.sc.security.JwtAuthenticationEntryPoint;
import com.github._1element.sc.security.JwtAuthenticationFilter;
import com.github._1element.sc.utils.URIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Web security configuration.
 * Access to all resources should be restricted.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  private UserDetailsService userDetailsService;

  private PasswordEncoder passwordEncoder;

  /**
   * Constructor.
   *
   * @param jwtAuthenticationEntryPoint the authentication entry point
   * @param userDetailsService the user details service
   * @param passwordEncoder the password encoder
   */
  @Autowired
  public WebSecurityConfiguration(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                                  UserDetailsService userDetailsService,
                                  PasswordEncoder passwordEncoder) {
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.userDetailsService = userDetailsService;
    this.passwordEncoder = passwordEncoder;
  }

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationTokenFilter() {
    return new JwtAuthenticationFilter();
  }

  @Autowired
  public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
    authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
      .csrf().disable()
      .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
      // don't create session, this is a stateless application
      .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
      .authorizeRequests()
      .antMatchers( URIConstants.API_ROOT + URIConstants.API_AUTH).permitAll()
      .anyRequest().authenticated();

    // custom JWT based security filter
    httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    // disable page caching
    httpSecurity.headers().cacheControl();
  }

}
