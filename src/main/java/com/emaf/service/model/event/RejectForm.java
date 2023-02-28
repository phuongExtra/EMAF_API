package com.emaf.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * RejectForm
 *
 * @author: VuongVT2
 * @since: 2022/12/05
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RejectForm {
    @NotBlank
    @Length(max = 10)
    private String eventId;

    @NotBlank
    private String feedBack;
}
