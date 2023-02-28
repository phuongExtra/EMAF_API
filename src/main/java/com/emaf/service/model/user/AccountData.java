package com.emaf.service.model.user;

import lombok.*;

/**
 * AccountData
 *
 * @author: VuongVT2
 * @since: 2022/12/03
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountData {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String studentCode;
    private String avatar;
    private String status;
    private String role;
}
