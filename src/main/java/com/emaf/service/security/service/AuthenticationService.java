package com.emaf.service.security.service;

import com.emaf.service.entity.User;
import com.emaf.service.enumeration.ERole;

/**
 * AuthenticationService
 *
 * @author khal
 * @since 2022/04/16
 */
public interface AuthenticationService {

    boolean checkUserLogin(String email, ERole eRole);


    User getUserByEmail(String email);

}
