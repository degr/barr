package com.scnsoft.permissions.security;

import com.scnsoft.permissions.security.jwt.JwtConfig;
import com.scnsoft.permissions.security.jwt.JwtTokenProvider;
import com.scnsoft.permissions.security.jwt.JwtUserDetailsService;
import com.scnsoft.permissions.security.jwt.JwtUserFactory;
import com.scnsoft.permissions.service.GroupService;
import com.scnsoft.permissions.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String SIGN_IN = "/users/signIn";
    private static final String SIGN_UP = "/users/signUp";
    private JwtConfig jwtConfig;

    @Autowired
    private void setUp(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(JwtUserDetailsService userDetailsService) {
        return new JwtTokenProvider(userDetailsService);
    }

    @Bean
    public JwtUserDetailsService jwtUserDetailsService(UserService userService,
                                                       JwtUserFactory jwtUserFactory) {
        return new JwtUserDetailsService(userService, jwtUserFactory);
    }

    @Bean
    public JwtUserFactory jwtUserFactory(GroupService groupService) {
        return new JwtUserFactory(groupService);
    }

    @Bean
    public JwtConfig jwtConfig(JwtTokenProvider provider) {
        return new JwtConfig(provider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(SIGN_IN).permitAll()
                .antMatchers(SIGN_UP).permitAll()
                .and()
                .apply(jwtConfig);
    }
}