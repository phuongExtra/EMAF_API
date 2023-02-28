package com.emaf.service.security;

import com.emaf.service.security.config.JwtAuthenticationEntryPoint;
import com.emaf.service.security.config.JwtSecurityConfigurer;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * SecurityConfig
 *
 * @author khal
 * @since 2021/10/03
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccessTokenService accessTokenService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(final AccessTokenService accessTokenService,
                          final UserDetailsServiceImpl userDetailsService,
                          final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.accessTokenService = accessTokenService;
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                var cors = new CorsConfiguration();
                cors.setAllowedOrigins(List.of("http://localhost:8082", "http://emaf-stg.quanochoa.com",
                        "http://127.0.0.1:5500", "http://localhost:8081", "http://localhost:8080"));
                cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                cors.setAllowedHeaders(List.of("Authorization", "Content-Type", "Host",
                        "User-Agent", "Accept", "Accept-Encoding", "Connection", "Keep-Alive", "Access-Control-Max-Age", "Content-Length",
                        "Access-Control-Allow-Headers", "Origin", "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers",
                        "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials", "headers"));
                cors.setAllowCredentials(true);
                return cors;
//                var cors = new CorsConfiguration();
//                cors.setAllowedOrigins(List.of("*"));
//                cors.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//                cors.setAllowedHeaders(List.of("*"));
//                return cors;
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter: off
        http
                .csrf()
                .disable()
                .cors()
                .configurationSource(corsConfigurationSource())
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/emaf/api/v1/auth/**").permitAll()
                .antMatchers("/emaf/api/v1/mock/**").permitAll()
                .antMatchers("/ws-emaf", "/ws-emaf/**").permitAll()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .apply(new JwtSecurityConfigurer(accessTokenService, userDetailsService));
    }
}
