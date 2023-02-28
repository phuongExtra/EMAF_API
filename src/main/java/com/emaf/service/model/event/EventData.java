package com.emaf.service.model.event;

import lombok.*;

/**
 * EventData
 *
 * @author: VuongVT2
 * @since: 2022/10/05
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventData {
    private Long id;
    private String eventName;
    private String room;
    private String status;
    private String startTime;
    private String endTime;
    private String host;
}
