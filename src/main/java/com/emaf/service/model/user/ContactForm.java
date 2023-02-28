package com.emaf.service.model.user;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * ContactForm
 *
 * @author: VuongVT2
 * @since: 2022/10/30
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContactForm {
    @NotBlank
    @Length(max = 100)
    private String fullName;

    @NotBlank
    @Length(max = 320)
    private String email;

    @NotBlank
    @Length(max = 2000)
    private String message;
}
