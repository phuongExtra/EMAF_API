package com.emaf.service.model.event;

import lombok.*;

import java.io.Serializable;

/**
 * ParticipantReq
 *
 * @author: VuongVT2
 * @since: 2022/11/09
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ParticipantReq implements Serializable {
    private String fullName;
    private String email;
    private Long eventParticipationId;
    private String status;
}
