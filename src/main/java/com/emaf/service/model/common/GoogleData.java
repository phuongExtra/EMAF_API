package com.emaf.service.model.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * GoogleData
 *
 * @author: VuongVT2
 * @since: 2022/05/20
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleData {
    private String id;
    private String email;
    private boolean verified_email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private String hd;
    private String locale;
//    private String userId;
//    private String email;
//    private boolean emailVerified;
//    private String name;
//    private String pictureUrl;
//    private String locale;
//    private String familyName;
//    private String givenName;
//
//    public GoogleData(GoogleIdToken.Payload payload) {
//        this.userId = payload.getSubject();
//        this.email = payload.getEmail();
//        this.emailVerified = payload.getEmailVerified();
//        this.name = (String) payload.get("name");
//        this.pictureUrl = (String) payload.get("picture");
//        this.locale = (String) payload.get("locale");
//        this.familyName = (String) payload.get("family_name");
//        this.givenName = (String) payload.get("given_name");
//    }

}
