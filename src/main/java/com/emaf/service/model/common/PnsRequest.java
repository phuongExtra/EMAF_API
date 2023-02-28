package com.emaf.service.model.common;

import lombok.*;

/**
 * PnsRequest
 *
 * @author: VuongVT2
 * @since: 2022/04/15
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PnsRequest {
    private String fcmToken;
    private String content;
    private String title;
    private String receiverId;
}
