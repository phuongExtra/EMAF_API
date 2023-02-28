package com.emaf.service.security.service;

import com.emaf.service.entity.User;
import com.emaf.service.entity.Role;
import com.emaf.service.enumeration.ERole;
import com.emaf.service.repository.RoleRepository;
import com.emaf.service.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * AuthenticationServiceImpl
 *
 * @author khal
 * @since 2022/04/16
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AuthenticationServiceImpl(final RoleRepository roleRepository,
                                     final UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean checkUserLogin(final String email, final ERole eRole) {
        Role role = roleRepository.findById(eRole)
                .orElse(null);
        return userRepository.findByEmailAndRole(email, role).isPresent();
    }

    @Override
    public User getUserByEmail(final String email) {
        return userRepository.getByEmail(email);
    }

}
