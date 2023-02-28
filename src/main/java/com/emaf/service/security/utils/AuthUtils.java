package com.emaf.service.security.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * AuthUtils
 *
 * @author khal
 * @since 2022/01/11
 */
public class AuthUtils {

    /**
     * Extract token from request
     *
     * @param request http servlet request
     * @return String - bearer token
     */
    public static String extractTokenFrom(HttpServletRequest request) {
        final String AUTH_TYPE = "Bearer";
        String authorizationHeader = request.getHeader("Authorization");

        if (StringUtils.hasLength(authorizationHeader) && authorizationHeader.startsWith(AUTH_TYPE)) {
            return authorizationHeader.substring(AUTH_TYPE.length() + 1, authorizationHeader.length());
        }

        return null;
    }

}
