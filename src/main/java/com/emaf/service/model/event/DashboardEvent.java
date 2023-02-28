package com.emaf.service.model.event;

import com.emaf.service.entity.Event;
import lombok.*;

import java.util.List;

/**
 * DashboardEvent
 *
 * @author: VuongVT2
 * @since: 2022/11/21
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DashboardEvent {
    private List<Event> yourRunningEvents;
    private List<Event> runningEvents;
    private List<Event> approvedEvents;
    private List<Event> finishedEvents;

}
