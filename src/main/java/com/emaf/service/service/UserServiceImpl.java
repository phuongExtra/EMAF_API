package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ExistenceException;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.logging.AppLogger;
import com.emaf.service.common.utils.IDGenerator;
import com.emaf.service.component.S3Component;
import com.emaf.service.dao.UserDAO;
import com.emaf.service.entity.Event;
import com.emaf.service.entity.Role;
import com.emaf.service.entity.User;
import com.emaf.service.enumeration.EOrganizationCommitteeStatus;
import com.emaf.service.enumeration.ERole;
import com.emaf.service.enumeration.EUserStatus;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.model.user.*;
import com.emaf.service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author: VuongVT2
 * @since: 30/10/2022 9:09 PM
 * @description:
 */
@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private Message message;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private S3Component s3Component;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationCommitteeRepository organizationCommitteeRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserDAO userDAO;

    @Override
    public User getProfile(final String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean updateProfile(final UserProfile userProfile, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));
        user.setFirstName(userProfile.getFirstName());
        user.setLastName(userProfile.getLastName());
        if (StringUtils.hasLength(userProfile.getPhoneNumber())) {
            user.setPhoneNumber(userProfile.getPhoneNumber());
        }
        if (StringUtils.hasLength(userProfile.getMajorId())) {
            user.setMajor(majorRepository.findById(userProfile.getMajorId())
                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())));
        }
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean createContact(final ContactForm contactForm) {
        return false;
    }

    @Override
    public String updateAvatar(final MultipartFile avatar, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData() + " User null"));
        try {
            if (StringUtils.hasLength(user.getAvatar())) {
                String fileName = user.getAvatar().substring(user.getAvatar().lastIndexOf("/") + 1);
                s3Component.delete("users/" + userId + "/avatar", fileName);
            }
            String avatarUser = s3Component.upload("users/" + userId + "/avatar", avatar);
            user.setAvatar(avatarUser);
            userRepository.save(user);
            return avatarUser;
        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }
    }

    @Override
    public List<User> getUserListByEmail(final String email, final String userId, final String eventId) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new ExistenceException(message.getWarnUserNotFound()));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ExistenceException(message.getWarnNoData()));
        List<User> userList = userRepository.findAllByLikeEmail(email)
                .stream()
                .filter(user -> !user.equals(currentUser))
                .filter(user -> user.getRole().getId().equals(ERole.ROLE_STUDENT))
                .filter(user -> !organizationCommitteeRepository.existsByUserAndEventAndStatus(user, event, EOrganizationCommitteeStatus.ACCEPTED))
                .filter(user -> !organizationCommitteeRepository.existsByUserAndEventAndStatus(user, event, EOrganizationCommitteeStatus.PENDING))
                .filter(user -> !organizationCommitteeRepository.existsByUserAndEventAndStatus(user, event, EOrganizationCommitteeStatus.REJECTED))
                .collect(Collectors.toList());
        return userList;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean createAccount(final AccountForm accountForm) {
        String id = IDGenerator.generateID(userRepository, 10);
        Role role = roleRepository.findById(ERole.valueOf(accountForm.getRole()))
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " role null"));
        User user = User.builder()
                .id(id)
                .email(accountForm.getEmail())
                .firstName(accountForm.getFirstName())
                .lastName(accountForm.getLastName())
                .role(role)
                .status(EUserStatus.ACTIVE)
                .build();
        userRepository.save(user);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean updateAccount(final AccountUpdateForm accountUpdateForm) {
        User user = userRepository.findById(accountUpdateForm.getUserId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " account null"));
        Role role = roleRepository.findById(ERole.valueOf(accountUpdateForm.getRole()))
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " role null"));
        user.setRole(role);
        user.setStatus(EUserStatus.valueOf(accountUpdateForm.getStatus()));
        userRepository.save(user);
        return true;
    }

    @Override
    public PagedResponse<AccountData> filterAccount(final String search, final String status, final String role, final Integer page, final Integer size) {
        List<User> users = userDAO.filterAccount(search, status, role, size, (page - 1) * size);
        List<AccountData> accountDataList = users.stream()
                .map(user -> {
                    AccountData accountData = AccountData.builder()
                            .id(user.getId())
                            .avatar(user.getAvatar())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .studentCode(user.getStudentCode())
                            .role(user.getRole().getName())
                            .status(user.getStatus().name())
                            .build();
                    return accountData;
                }).collect(Collectors.toList());
        long totalElements = userDAO.countFilterAccount(search, status, role);
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        return new PagedResponse<>(accountDataList, page, size, totalElements, totalPages);
    }

}

