package com.emaf.service.model.common;

import com.emaf.service.enumeration.EEventType;
import lombok.*;

import java.util.List;

/**
 * EventFormCreate
 *
 * @author: VuongVT2
 * @since: 2022/10/04
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventFormCreate {
    private String eventName;
    private String shortDescription;
    private String description;
    private EEventType type;
    private String startTime;
    private String endTime;
    private List<CommonData> eventMajors;
}


