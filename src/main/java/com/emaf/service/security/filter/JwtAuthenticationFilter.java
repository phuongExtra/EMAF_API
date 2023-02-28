package com.emaf.service.security.filter;

import com.emaf.service.common.logging.AppLogger;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.security.service.UserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * JwtAuthenticationFilter
 *
 * @author khale
 * @since 2021/10/23
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final static String AUTH_TYPE = "Bearer";

    private final AccessTokenService accessTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(final AccessTokenService accessTokenService,
                                   final UserDetailsServiceImpl userDetailsService) {
        this.accessTokenService = accessTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (Objects.nonNull(token) && accessTokenService.validateAccessToken(token)) {
                String username = accessTokenService.getUserNameFromAccessToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception e) {
            AppLogger.errorLog("Cannot set user authentication " + e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (StringUtils.hasLength(authorizationHeader) && authorizationHeader.startsWith(AUTH_TYPE)) {
            return authorizationHeader.substring(AUTH_TYPE.length() + 1, authorizationHeader.length());
        }

        return null;
    }
}
