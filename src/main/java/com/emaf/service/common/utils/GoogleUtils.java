package com.emaf.service.common.utils;

import com.emaf.service.common.constant.GoogleConstant;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.emaf.service.model.common.GoogleData;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GoogleUtils
 *
 * @author: VuongVT2
 * @since: 2022/05/20
 */
@Component
public class GoogleUtils {

    private final GoogleConstant googleConstant;

    public GoogleUtils(final GoogleConstant googleConstant) {
        this.googleConstant = googleConstant;
    }

    public String getToken(final String code) throws IOException {
        String link = googleConstant.getGoogleLinkGetToken();

        String response = Request.Post(link)
                .bodyForm(Form.form().add("client_id", googleConstant.getGoogleAppId())
                        .add("client_secret", googleConstant.getGoogleAppSecret())
                        .add("redirect_uri", googleConstant.getGoogleRedirectUri()).add("code", code)
                        .add("grant_type", "authorization_code").build())
                .execute().returnContent().asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response).get("access_token");
        return node.textValue();
    }

    public String getTokenMobile(final String code) throws IOException {
        String link = googleConstant.getGoogleLinkGetToken();

        String response = Request.Post(link)
                .bodyForm(Form.form().add("client_id", googleConstant.getGoogleAppId())
                        .add("client_secret", googleConstant.getGoogleAppSecret())
                        .add("redirect_uri", "http://localhost:8082/emaf/api/v1/auth/login-google-mobile").add("code", code)
                        .add("grant_type", "authorization_code").build())
                .execute().returnContent().asString();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response).get("access_token");
        return node.textValue();
    }

    public GoogleData getUserInfo(final String accessToken) throws IOException {
        String link = googleConstant.getGoogleLinkGetUserInfo() + accessToken;
        String response = Request.Get(link).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        GoogleData googleData = mapper.readValue(response, GoogleData.class);
        System.out.println(googleData);
        return googleData;
    }

    public UserDetails buildUser(GoogleData googleData,String role) {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role));
        UserDetails userDetail = new User(googleData.getEmail(),
                "", enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return userDetail;
    }
}
