package com.kt.myrestapi.config;

import com.kt.myrestapi.accounts.AccountService;
import com.kt.myrestapi.filter.CustomAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    AccountService accountService;
    @Autowired
    Environment env;
    //    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.anonymous()
//                .and()
//                .formLogin()
//                .and()
//                .authorizeRequests()
//                .mvcMatchers(HttpMethod.GET, "/api/**")
//                .permitAll()
//                .anyRequest()
//                .authenticated();
//    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/api/**")
                .permitAll()
                .antMatchers("/*/login", "/*/signup")
                .permitAll().and()
                .addFilter(getAuthenticationFilter());
        http.headers().frameOptions().disable();
    }

    private CustomAuthenticationFilter getAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter authenticationFilter =
                new CustomAuthenticationFilter(authenticationManager(), accountService, env);
        return authenticationFilter;
    }
}
