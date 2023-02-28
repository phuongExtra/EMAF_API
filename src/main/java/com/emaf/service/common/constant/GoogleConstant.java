package com.emaf.service.common.constant;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * GoogleConstant
 *
 * @author: VuongVT2
 * @since: 2022/05/20
 */
@Getter
@Component
@PropertySource(value = "classpath:google-${spring.profiles.active}.properties", encoding = "utf-8")
public class GoogleConstant {

    @Value("${google.app.id}")
    private String googleAppId;

    @Value("${google.app.secret}")
    private String googleAppSecret;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    @Value("${google.link.get.token}")
    private String googleLinkGetToken;

    @Value("${google.link.get.user_info}")
    private String googleLinkGetUserInfo;

    @Value("${google.process.login}")
    private String googleProcessLogin;
}
