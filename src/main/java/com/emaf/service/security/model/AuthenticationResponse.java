package com.emaf.service.security.model;

import lombok.*;

import java.io.Serializable;

/**
 * AuthenticationResponse
 *
 * @author khale
 * @since 2021/10/23
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse implements Serializable {

    private String userId;
    private String avatar;
    private String username;
    private String fullName;
    private String accessToken;
    private String role;
    private String expirationTime;

}
