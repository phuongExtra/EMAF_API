package com.emaf.service.controller;

import com.emaf.service.common.constant.AppConstant;
import com.emaf.service.entity.User;
import com.emaf.service.model.common.DataWrapper;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.model.user.*;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Arrays;
import java.util.List;

/**
 * UserController
 *
 * @author: KhaL
 * @since: 2022/01/11
 */
@Validated
@RestController
@RequestMapping(value = "/emaf/api/v1/user")
public class UserController {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/my-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getMyProfile(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return userService.getProfile(userId);
    }

    @PostMapping(value = "/edit-profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateProfile(@RequestBody UserProfile userProfile, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return userService.updateProfile(userProfile, userId);
    }

    @PostMapping(value = "/create-contact", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean createContact(@Valid @RequestBody ContactForm contactForm) {
        return userService.createContact(contactForm);
    }

    @PostMapping(value = "/update-avatar", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateAvatar(@RequestParam(value = "avatar") MultipartFile avatar,
                               HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return userService.updateAvatar(avatar, userId);
    }

    @GetMapping(value = "/list-user")
    public List<User> getUserListByEmail(@RequestParam(value = "email") String email,
                                         @RequestParam(value = "eventId") String eventId,
                                         HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return userService.getUserListByEmail(email, userId, eventId);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/create-account", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean createAccount(@Valid @RequestBody AccountForm accountForm) {
        return userService.createAccount(accountForm);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping(value = "/update-account", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateAccount(@Valid @RequestBody AccountUpdateForm accountUpdateForm) {
        return userService.updateAccount(accountUpdateForm);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<AccountData> filterAccount(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                    @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
                                                    @RequestParam(name = "role", defaultValue = AppConstant.DEFAULT_STR_VALUE) String role,
                                                    @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                    @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        return userService.filterAccount(search, status, role, page, size);
    }
}