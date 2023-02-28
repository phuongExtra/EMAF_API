package com.emaf.service.service;

import com.emaf.service.entity.User;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.model.user.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author: VuongVT2
 * @since: 30/10/2022 9:13 PM
 * @description:  
 */
public interface UserService {

    User getProfile(String userId);

    boolean updateProfile(UserProfile userProfile, String userId);

    boolean createContact(ContactForm contactForm);

    String updateAvatar(MultipartFile avatar, String userId);

    List<User> getUserListByEmail(String email, String userId, final String eventId);

    boolean createAccount(AccountForm accountForm);

    boolean updateAccount(AccountUpdateForm accountUpdateForm);

    PagedResponse<AccountData> filterAccount(String search, String status, String role, Integer page, Integer size);
}
