package com.projectcondor.condor.config;

import com.projectcondor.condor.security.JwtConfigurer;
import com.projectcondor.condor.security.JwtTokenProvider;
import com.projectcondor.condor.service.MyUserDetailsService;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final MyUserDetailsService myUserDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, MyUserDetailsService myUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(myUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .antMatchers("/api/auth/**").permitAll()
                        .antMatchers("/error").permitAll()
                        .antMatchers("/api/public/**").permitAll()
                        .anyRequest().authenticated())
                .apply(new JwtConfigurer(jwtTokenProvider));

        return http.build();
    }

}