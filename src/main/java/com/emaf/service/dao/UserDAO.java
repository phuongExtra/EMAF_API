package com.emaf.service.dao;

import com.emaf.service.entity.User;

import java.util.List;

/**
 * UserDAO
 *
 * @author: VuongVT2
 * @since: 2022/12/03
 */
public interface UserDAO {
    List<User> filterAccount(String search, String status, String role, long limit, long offset);

    long countFilterAccount(String search, String status, String role);
}
