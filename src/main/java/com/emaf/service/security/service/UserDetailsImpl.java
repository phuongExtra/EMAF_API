package com.emaf.service.security.service;

import com.emaf.service.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

/**
 * UserDetailsImpl
 *
 * @author khale
 * @since 2021/10/22
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private String id;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(final User user) {
        List<GrantedAuthority> authorities = Arrays.asList(new GrantedAuthority[] {
                new SimpleGrantedAuthority(user.getRole().getId().name())
        });

        return new UserDetailsImpl(user.getId(), user.getEmail(), authorities);
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
