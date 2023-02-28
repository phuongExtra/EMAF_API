package com.emaf.service.model.event;

import com.emaf.service.entity.Room;
import lombok.*;

/**
 * EventRoomData
 *
 * @author: VuongVT2
 * @since: 2022/11/18
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EventRoomData {
    private Room room;
    private String eventId;
    private String eventName;
    private String startTime;
    private String endTime;
}
