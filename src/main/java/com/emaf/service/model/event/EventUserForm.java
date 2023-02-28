package com.emaf.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * EventUserForm
 *
 * @author: VuongVT2
 * @since: 2022/10/15
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventUserForm {
    private String userId;
    private String roleId;
}
