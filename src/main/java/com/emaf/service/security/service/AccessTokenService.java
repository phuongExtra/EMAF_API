package com.emaf.service.security.service;

import javax.servlet.http.HttpServletRequest;

/**
 * AccessTokenService
 *
 * @author khale
 * @since 2021/10/22
 */
public interface AccessTokenService {

    /**
     * Generate access token from username in UserDetails
     *
     * @param userDetails
     * @return String - new access token
     */
    String generateAccessToken(UserDetailsImpl userDetails);

    /**
     * Generate access token from username
     *
     * @param username
     * @return String - new access token is generated from username
     */
    String generateAccessTokenFromUser(String id, String username, String role);

    /**
     * Get user role from token
     *
     * @param request
     * @return String - user's role
     */
    String getUserID(final HttpServletRequest request);

    /**
     * Get user role from token
     *
     * @param request
     * @return String - user's role
     */
    String getUserRole(final HttpServletRequest request);

    /**
     * Get username from access token
     *
     * @param accessToken
     * @return String - the username is extracted from access token
     */
    String getUserNameFromAccessToken(String accessToken);

    /**
     * Validate access token
     *
     * @param accessToken
     * @return boolean - the validation result after checking the access token
     */
    boolean validateAccessToken(String accessToken);

}
