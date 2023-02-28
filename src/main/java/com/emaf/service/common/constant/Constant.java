package com.emaf.service.common.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Constant
 *
 * @author khale
 * @since 2021/11/04
 */
@Getter
@Component
@PropertySource(value = "classpath:constant-${spring.profiles.active}.properties", encoding = "utf-8")
public class Constant {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.host}")
    private String appHost;

    @Value("${user.verification.expire}")
    private long userVerificationExpire;

    @Value("${app.redirect_uri.google}")
    private String appRedirectURIGoogle;

}
