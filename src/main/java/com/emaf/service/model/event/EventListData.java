package com.emaf.service.model.event;

import com.emaf.service.entity.Room;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.enumeration.EEventType;
import com.emaf.service.model.common.CommonData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * EventListData
 *
 * @author khal
 * @since 2023/02/01
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventListData {

    private String eventName;
    private String organizerName;
    private String location;
    private List<CommonData> room;
    private int minNumOfParticipant;
    private int maxNumOfParticipant;
    private int actualNumOfParticipant;
    private EEventStatus Status;
    private EEventType type;
    private String startTime;
}
