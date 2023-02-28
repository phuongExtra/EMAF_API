package com.emaf.service.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * EventTimelineCreate
 *
 * @author: VuongVT2
 * @since: 2022/10/15
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventTimelineCreate {
    private String content;
    private String note;
    private String activity;
    private String startTime;
    private String endTime;
}
