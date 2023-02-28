package com.emaf.service.model;

import lombok.*;

import java.util.Date;

/**
 * ParticipationData
 *
 * @author: VuongVT2
 * @since: 2022/12/17
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ParticipationData {
    private String studentCode;
    private String fullName;
    private String email;
    private String eventName;
    private String role;
    private Date checkinTime;
    private Date checkoutTime;
    private String status;


}
