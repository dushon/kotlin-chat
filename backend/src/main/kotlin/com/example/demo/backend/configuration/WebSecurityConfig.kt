package com.example.demo.backend.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http
                .authorizeRequests()
                .antMatchers("/**", "/js/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .headers()
                .contentSecurityPolicy("worker-src 'self'")
    }
}
