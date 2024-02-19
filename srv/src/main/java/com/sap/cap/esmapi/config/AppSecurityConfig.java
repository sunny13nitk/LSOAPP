package com.sap.cap.esmapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;

import com.sap.cap.esmapi.utilities.constants.GC_Constants;
import com.sap.cloud.security.xsuaa.XsuaaServiceConfiguration;
import com.sap.cloud.security.xsuaa.extractor.IasXsuaaExchangeBroker;
import com.sap.cloud.security.xsuaa.token.TokenAuthenticationConverter;
import com.sap.cloud.security.xsuaa.tokenflows.XsuaaTokenFlows;

@Configuration
@EnableWebSecurity
// @Order(1) // needs to have higher priority than CAP security config
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@EnableAsync
public class AppSecurityConfig
{

  @Autowired
  private XsuaaServiceConfiguration xsuaaServiceConfiguration;

  @Autowired
  XsuaaTokenFlows xsuaaTokenFlows;

  @Bean
  @Profile(GC_Constants.gc_LocalProfile)
  public SecurityFilterChain appFilterChain(HttpSecurity http) throws Exception
  {

    /*
     * ----------- Local Testing --------------------
     */

    http.logout((logout) -> logout.logoutSuccessUrl("/logout/").permitAll()).authorizeRequests()
        .antMatchers(HttpMethod.GET, "/static/**").permitAll();
    http.requestMatchers().antMatchers("/api/**").antMatchers("/esslocal/**").antMatchers("/poclocal/**").and().csrf()
        .disable() // don't insist on csrf tokens in put, post etc.
        .authorizeRequests().anyRequest().permitAll();
    return http.build();

  }

  @Bean
  @Profile(GC_Constants.gc_BTPProfile)
  public SecurityFilterChain appFilterChainforTest(HttpSecurity http) throws Exception
  {

    // /*
    // * ----------- CF Deployment --------------------
    // */

    // @formatter:off
    HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(
        new ClearSiteDataHeaderWriter(Directive.ALL));
    http.logout((logout) -> logout.logoutSuccessUrl("/logout/").permitAll())
        .logout((logout) -> logout.addLogoutHandler(clearSiteData)).sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        // session is created by approuter
        .and().authorizeRequests() // authorize all requests
        .antMatchers("/login/**").permitAll().antMatchers(HttpMethod.GET, "/static/**").permitAll()
        .antMatchers(HttpMethod.GET, "/static/images/**").permitAll().antMatchers(HttpMethod.GET, "/static/css/**")
        .permitAll().antMatchers("/web-components.js/**").permitAll().antMatchers(HttpMethod.GET, "/static/js/**")
        .permitAll().antMatchers("/ess/**").authenticated() // Only
        .antMatchers("/lso/**").authenticated()
        //.antMatchers("/lso/**").hasAnyAuthority(GC_Constants.gc_role_employee_lso, GC_Constants.gc_role_contractor_lso)
        .anyRequest().denyAll().and().oauth2ResourceServer()
        .bearerTokenResolver(new IasXsuaaExchangeBroker(xsuaaTokenFlows)).jwt()
        .jwtAuthenticationConverter(getJwtAuthoritiesConverter());
    // @formatter:on

    return http.build();

  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() throws Exception
  {
    return (web) -> web.ignoring().antMatchers("/static/**").antMatchers("/images/**").antMatchers("/css/**")
        .antMatchers("/js/**").antMatchers("/logout/**");

  }

  // /*
  // ----------- CF Deployment --------------------
  // */
  Converter<Jwt, AbstractAuthenticationToken> getJwtAuthoritiesConverter()
  {
    TokenAuthenticationConverter converter = new TokenAuthenticationConverter(xsuaaServiceConfiguration);
    converter.setLocalScopeAsAuthorities(true);
    return converter;
  }

}
