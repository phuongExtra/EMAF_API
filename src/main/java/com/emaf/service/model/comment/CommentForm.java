package com.emaf.service.model.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * CommentForm
 *
 * @author: VuongVT2
 * @since: 2022/10/23
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentForm {
    @NotBlank
    @Length(max = 10)
    private String userId;
    private Long commentId;
    @NotBlank
    private String content;
    @NotBlank
    private String eventId;
    @NotNull
    private Long parentId;
}
