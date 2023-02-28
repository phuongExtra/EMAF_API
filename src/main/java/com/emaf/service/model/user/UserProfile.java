package com.emaf.service.model.user;

import com.emaf.service.entity.Major;
import com.emaf.service.entity.Role;
import com.emaf.service.enumeration.EUserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * UserProfile
 *
 * @author: VuongVT2
 * @since: 2022/10/23
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @NotBlank
    @Length(max = 150)
    private String firstName;

    @NotBlank
    @Length(max = 150)
    private String lastName;

    @Length(max = 12)
    private String phoneNumber;

    @Length(max = 10)
    private String majorId;
}
