package com.emaf.service.security.controller;

import com.emaf.service.common.constant.AppConstant;
import com.emaf.service.common.constant.Constant;
import com.emaf.service.common.constant.GoogleConstant;
import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.utils.GoogleUtils;
import com.emaf.service.common.utils.IDGenerator;
import com.emaf.service.entity.User;
import com.emaf.service.enumeration.ERole;
import com.emaf.service.enumeration.EUserStatus;
import com.emaf.service.model.common.GoogleData;
import com.emaf.service.repository.RoleRepository;
import com.emaf.service.repository.UserRepository;
import com.emaf.service.security.model.AuthenticationResponse;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.security.service.AuthenticationService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * AuthenticationController
 *
 * @author khale
 * @since 2021/10/22
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/auth")
public class AuthenticationController {

    private final Constant constant;
    private final Message message;
    private final GoogleConstant googleConstant;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenService accessTokenService;
    private final AuthenticationService authenticationService;

    private final GoogleUtils googleUtils;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthenticationController(final Constant constant,
                                    final Message message,
                                    final GoogleConstant googleConstant,
                                    final AuthenticationManager authenticationManager,
                                    final AccessTokenService accessTokenService,
                                    final AuthenticationService authenticationService,
                                    final GoogleUtils googleUtils,
                                    final UserRepository userRepository,
                                    final RoleRepository roleRepository) {
        this.constant = constant;
        this.message = message;
        this.googleConstant = googleConstant;
        this.authenticationManager = authenticationManager;
        this.accessTokenService = accessTokenService;
        this.authenticationService = authenticationService;
        this.userRepository = userRepository;
        this.googleUtils = googleUtils;
        this.roleRepository = roleRepository;
    }


    @GetMapping(value = "/process-login-google", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processLoginGoogle(@RequestParam("accessTokenGoogle") @NotBlank String accessTokenGoogle,
                                                HttpServletRequest request) throws IOException {

        GoogleData googleData = googleUtils.getUserInfo(accessTokenGoogle);
        String role;
        String expirationTime = LocalDateTime.now().plusMinutes(constant.getJwtExpiration()).format(DateTimeFormatter.ofPattern(AppConstant.DTF_dd_MM_yyyy_HH_mm_ss));
        User user = authenticationService.getUserByEmail(googleData.getEmail());

        String id;
        if (Objects.nonNull(user)) {
            id = user.getId();
            role = user.getRole().getId().name();
        } else {
            id = IDGenerator.generateID(userRepository, 10);
            user = User.builder()
                    .id(id)
                    .avatar(googleData.getPicture())
                    .firstName(googleData.getGiven_name())
                    .lastName(googleData.getFamily_name())
                    .email(googleData.getEmail())
                    .role(roleRepository.getById(ERole.ROLE_STUDENT))
                    .status(EUserStatus.ACTIVE)
                    .build();
            userRepository.saveAndFlush(user);
            role = ERole.ROLE_STUDENT.name();
        }
        UserDetails userDetail = googleUtils.buildUser(googleData, user.getRole().getId().name());


        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, googleData.getEmail(),
                userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String accessToken = accessTokenService.generateAccessToken(userDetails);
        String accessToken = accessTokenService.generateAccessTokenFromUser(id, googleData.getEmail(), role);


        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .userId(id)
                .avatar(user.getAvatar())
                .fullName(user.getLastName() + " " + user.getFirstName())
                .username(googleData.getEmail())
                .accessToken(accessToken)
                .role(role)
                .expirationTime(expirationTime)
                .build();
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping(value = "/login-google-mobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?>  loginGoogleMobile(@RequestParam("code") @NotBlank String code, HttpServletRequest request) throws IOException {
        String accessTokenGoogle = googleUtils.getTokenMobile(code);
        GoogleData googleData = googleUtils.getUserInfo(accessTokenGoogle);
        String role;
        String expirationTime = LocalDateTime.now().plusMinutes(constant.getJwtExpiration()).format(DateTimeFormatter.ofPattern(AppConstant.DTF_dd_MM_yyyy_HH_mm_ss));
        User user = authenticationService.getUserByEmail(googleData.getEmail());

        String id;
        if (Objects.nonNull(user)) {
            id = user.getId();
            role = user.getRole().getId().name();
        } else {
            id = IDGenerator.generateID(userRepository, 10);
            user = User.builder()
                    .id(id)
                    .avatar(googleData.getPicture())
                    .firstName(googleData.getGiven_name())
                    .lastName(googleData.getFamily_name())
                    .email(googleData.getEmail())
                    .role(roleRepository.getById(ERole.ROLE_STUDENT))
                    .status(EUserStatus.ACTIVE)
                    .build();
            userRepository.saveAndFlush(user);
            role = ERole.ROLE_STUDENT.name();
        }
        UserDetails userDetail = googleUtils.buildUser(googleData, user.getRole().getId().name());


        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, googleData.getEmail(),
                userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        String accessToken = accessTokenService.generateAccessToken(userDetails);
        String accessToken = accessTokenService.generateAccessTokenFromUser(id, googleData.getEmail(), role);


        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .userId(id)
                .avatar(user.getAvatar())
                .fullName(user.getLastName() + " " + user.getFirstName())
                .username(googleData.getEmail())
                .accessToken(accessToken)
                .role(role)
                .expirationTime(expirationTime)
                .build();
        return ResponseEntity.ok(authResponse);

    }

    @GetMapping(value = "/login-google", produces = MediaType.APPLICATION_JSON_VALUE)
    public RedirectView loginGoogle(@RequestParam("code") @NotBlank String code, HttpServletResponse response) throws IOException {
        String accessTokenGoogle = googleUtils.getToken(code);
        RedirectView redirectView = new RedirectView();
        String url = new StringBuilder("http://localhost:8080")
                .append(String.format(constant.getAppRedirectURIGoogle(), accessTokenGoogle))
                .toString();
        redirectView.setUrl(url);
        return redirectView;

    }

    @GetMapping(value = "/login-mobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginMobile(@RequestParam("email") @NotBlank String email,
                                         HttpServletRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));

        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().getId().name()));
        UserDetails userDetail = new org.springframework.security.core.userdetails.User(user.getEmail(),
                "", true, true, true, true, authorities);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, user.getEmail(),
                userDetail.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = accessTokenService.generateAccessTokenFromUser(user.getId(), user.getEmail(), user.getRole().getId().name());
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .username(user.getEmail())
                .accessToken(accessToken)
                .role(user.getRole().getId().name())
                .expirationTime("")
                .build();
        return ResponseEntity.ok(authResponse);

    }
}
