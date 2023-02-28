package com.emaf.service.model.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * AccountUpdateForm
 *
 * @author: VuongVT2
 * @since: 2022/12/01
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountUpdateForm {
    @NotBlank
    @Length(max = 10)
    private String userId;

    @NotBlank
    @Length(max = 20)
    private String role;

    @NotBlank
    @Length(max = 20)
    private String status;
}
