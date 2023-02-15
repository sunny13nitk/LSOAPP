package com.sap.cap.esmapi.config;

import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.extractor.IasXsuaaExchangeBroker;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(1) // needs to have higher priority than CAP security config
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class AppSecurityConfig
{

  //  @Autowired
  //  private XsuaaServiceConfiguration xsuaaServiceConfiguration;

  //  @Autowired
  //  XsuaaTokenFlows xsuaaTokenFlows;

  @Bean
  public SecurityFilterChain appFilterChain(HttpSecurity http) throws Exception 
  {

    /*
      ----------- Local Testing --------------------
    */
     return http
      .requestMatchers()
      .antMatchers("/api/**")
      .antMatchers("/esslocal/**")
      .and().csrf().disable() // don't insist on csrf tokens in put, post etc.
      .authorizeRequests().anyRequest().permitAll().and()
      .build();
      

    
    /*
      ----------- CF Deployment --------------------
    */
      
      //    // @formatter:off
      //     http
      //     .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      // .and()
      //     .authorizeRequests()
      //     .antMatchers("/api/**").hasAuthority("Administrators")
      //     .antMatchers("/ess/**").authenticated()
      //     //.anyRequest().permitAll()
      //     .anyRequest().denyAll()
      // .and()
      //     .oauth2ResourceServer()
      //     .bearerTokenResolver(new IasXsuaaExchangeBroker(xsuaaTokenFlows))
      //     .jwt()
      //     .jwtAuthenticationConverter(getJwtAuthoritiesConverter());
      // // @formatter:on

      // return http.build(); 


  }

  // /*
  //     ----------- CF Deployment --------------------
  //   */
  //  Converter<Jwt, AbstractAuthenticationToken> getJwtAuthoritiesConverter() 
  //  {
  //      TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
	//  	   converter.setLocalScopeAsAuthorities(true);
	//  	   return converter;
  //  }

 

 
}
