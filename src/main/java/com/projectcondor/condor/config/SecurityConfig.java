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
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final MyUserDetailsService myUserDetailsService;  // Agregamos el UserDetailsService

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, MyUserDetailsService myUserDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.myUserDetailsService = myUserDetailsService; // Inyectamos el UserDetailsService
    }

    /*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*//*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*//*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*/@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // Configuramos el AuthenticationManager para usar el UserDetailsService y BCryptPasswordEncoder
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }

    /*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*//*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*//*~~(Migrate manually based on https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)~~>*/@SuppressWarnings("deprecation")
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic(basic -> basic.disable())  // Desactivamos la autenticación básica
                .csrf(csrf -> csrf.disable())  // Desactivamos CSRF, ya que usamos tokens JWT
                .sessionManagement(management -> management
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(requests -> requests
                        .antMatchers("/api/auth/**").permitAll()  // Rutas abiertas para autenticación
                        .anyRequest().authenticated())
                .apply(new JwtConfigurer(jwtTokenProvider));  // Añadimos nuestro filtro de JWT
    }
}
