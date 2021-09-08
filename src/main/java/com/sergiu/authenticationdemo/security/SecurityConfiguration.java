package com.sergiu.authenticationdemo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private JwtFilter jwtFilter;

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//    	PasswordEncoder encoder = 
//          PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    	auth
//          .inMemoryAuthentication()
//          .withUser("user")
//          .password(encoder.encode("password"))
//          .roles("USER")
//          .and()
//          .withUser("admin")
//          .password(encoder.encode("admin"))
//          .roles("USER", "ADMIN");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .csrf().disable()
          .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
          .authorizeRequests()
          	.antMatchers("/api/v1/authentication/login").permitAll()
          	.antMatchers("/api/v1/user/details").hasAuthority("ADMIN")
          .anyRequest()
          .authenticated()
          .and()
          .httpBasic();
    }
}
