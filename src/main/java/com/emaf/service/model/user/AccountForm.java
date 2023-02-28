package com.emaf.service.model.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * AccountForm
 *
 * @author: VuongVT2
 * @since: 2022/11/29
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountForm {
    @NotBlank
    @Length(max = 320)
    private String email;

    @NotBlank
    @Length(max = 150)
    private String firstName;

    @NotBlank
    @Length(max = 150)
    private String lastName;

    @NotBlank
    @Length(max = 20)
    private String role;
}
