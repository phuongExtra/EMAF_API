package com.emaf.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DocumentForm
 *
 * @author: VuongVT2
 * @since: 2022/11/07
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentForm {
    @NotBlank
    @Length(max = 10)
    private String eventId;
    private MultipartFile[] documents;
}
