package com.emaf.service.model.EventConfig;

import com.emaf.service.enumeration.EEventConfigurationStatus;
import lombok.*;

/**
 * EventConfigCreateForm
 *
 * @author: PhuongLN
 * @since: 2023/01/04
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventConfigCreateForm {
    private Long id;
    private Long requestLimitPeriod;
    private Long checkinOpenTime;
    private Long checkoutOpenTime;
    private Double participationThreshold;
    private String status;
}