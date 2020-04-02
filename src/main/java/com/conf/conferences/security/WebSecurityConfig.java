package com.conf.conferences.security;

import com.conf.conferences.security.jwt.JwtAuthenticationEntryPoint;
import com.conf.conferences.security.jwt.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableOAuth2Client
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService userService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    @Qualifier("oauth2ClientContext")
    OAuth2ClientContext oauth2ClientContext;

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter> oauth2ClientFilterRegistration(
            OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    private Filter oauth2ClientFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();

        filters.add(getClientFilter("/login/google", google(), googleResource()));
        filters.add(getClientFilter("/login/github", github(), githubResource()));
        filters.add(getClientFilter("/login/facebook", facebook(), facebookResource()));
        filters.add(getClientFilter("/login/linkedin", linkedin(), linkedinResource()));

        filter.setFilters(filters);
        return filter;
    }

    private Filter getClientFilter(String defaultFilterProcessUrl,
                                   AuthorizationCodeResourceDetails authorizationCodeResourceDetails,
                                   ResourceServerProperties resourceServerProperties) {
        OAuth2ClientAuthenticationProcessingFilter oauth2ClientFilter =
                new OAuth2ClientAuthenticationProcessingFilter(defaultFilterProcessUrl);

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(authorizationCodeResourceDetails, oauth2ClientContext);
        oauth2ClientFilter.setRestTemplate(restTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(resourceServerProperties.getUserInfoUri(),
                authorizationCodeResourceDetails.getClientId());
        tokenServices.setRestTemplate(restTemplate);
        oauth2ClientFilter.setTokenServices(tokenServices);
        oauth2ClientFilter.setAllowSessionCreation(false);
        oauth2ClientFilter.setAuthenticationSuccessHandler(oAuth2AuthenticationSuccessHandler);
        return oauth2ClientFilter;
    }

    @Bean
    @ConfigurationProperties("google.client")
    public AuthorizationCodeResourceDetails google() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("google.resource")
    public ResourceServerProperties googleResource() {
        return new ResourceServerProperties();
    }

    @Bean
    @ConfigurationProperties("github.client")
    public AuthorizationCodeResourceDetails github() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("github.resource")
    public ResourceServerProperties githubResource() {
        return new ResourceServerProperties();
    }

    @Bean
    @ConfigurationProperties("facebook.client")
    public AuthorizationCodeResourceDetails facebook() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("facebook.resource")
    public ResourceServerProperties facebookResource() {
        return new ResourceServerProperties();
    }

    @Bean
    @ConfigurationProperties("linkedin.client")
    public AuthorizationCodeResourceDetails linkedin() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("linkedin.resource")
    public ResourceServerProperties linkedinResource() {
        return new ResourceServerProperties();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                // dont authenticate this particular request
                .authorizeRequests().antMatchers("/authenticate").permitAll()
                .antMatchers("/login/google").permitAll()
                .antMatchers("/login/github").permitAll()
                .antMatchers("/login/facebook").permitAll()
                .antMatchers("/login/linkedin").permitAll()
                .antMatchers("/v2/api-docs", "/configuration/**", "/swagger*/**", "/webjars/**").permitAll()
                .antMatchers("/").permitAll().
                // all other requests need to be authenticated
                        anyRequest().authenticated().and().
                // make sure we use stateless session; session won't be used to
                // store user's state.
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .sessionManagement()
                .enableSessionUrlRewriting(true)
                .sessionCreationPolicy(SessionCreationPolicy.NEVER);

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(oauth2ClientFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }


}