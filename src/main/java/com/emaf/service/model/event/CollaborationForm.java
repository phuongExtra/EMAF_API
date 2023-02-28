package com.emaf.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * CollaborationForm
 *
 * @author: VuongVT2
 * @since: 2022/11/09
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CollaborationForm {
    @NotBlank
    private String eventId;
    @NotEmpty
    private List<String> userIdList;

}
