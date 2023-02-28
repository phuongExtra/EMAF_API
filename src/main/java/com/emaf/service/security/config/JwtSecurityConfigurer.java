package com.emaf.service.security.config;

import com.emaf.service.security.filter.JwtAuthenticationFilter;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.security.service.UserDetailsServiceImpl;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * JwtSecurityConfigurer
 *
 * @author khale
 * @since 2021/10/23
 */
public class JwtSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final AccessTokenService accessTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtSecurityConfigurer(final AccessTokenService accessTokenService,
                                 final UserDetailsServiceImpl userDetailsService) {
        this.accessTokenService = accessTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new JwtAuthenticationFilter(accessTokenService, userDetailsService), UsernamePasswordAuthenticationFilter.class);
    }

}
