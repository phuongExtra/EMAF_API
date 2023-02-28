package com.emaf.service.service;

import com.emaf.service.entity.EventRoomSchedule;
import com.emaf.service.entity.Room;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.model.event.EventRoomData;
import com.emaf.service.repository.EventRoomScheduleRepository;
import com.emaf.service.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RoomServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/10/09
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EventRoomScheduleRepository eventRoomScheduleRepository;

    @Override
    public List<Room> getAllRoom() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> getAllRoomAvailable(final String startTime, final String endTime) {
        List<Room> roomList = roomRepository.getRoomAvailable(startTime, endTime, Arrays.asList(EEventStatus.APPROVED.name(), EEventStatus.RUNNING.name()));
        return roomList;
    }

    @Override
    public List<EventRoomData> getAllEventRoomBy(final String date) {
        //yyyyMMddHHss
        List<EventRoomSchedule> eventRoomSchedules = eventRoomScheduleRepository.getAllByDate(date + "0500", date + "2100", Arrays.asList(EEventStatus.APPROVED, EEventStatus.RUNNING));
        List<EventRoomData> eventRoomDataList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(eventRoomSchedules)) {
            eventRoomDataList = eventRoomSchedules.stream()
                    .map(eventRoomSchedule -> {
                        EventRoomData eventRoomData = EventRoomData.builder()
                                .room(eventRoomSchedule.getRoom())
                                .eventId(eventRoomSchedule.getEvent().getId())
                                .eventName(eventRoomSchedule.getEvent().getEventName())
                                .startTime(eventRoomSchedule.getEvent().getStartTime())
                                .endTime(eventRoomSchedule.getEvent().getEndTime())
                                .build();
                        return eventRoomData;
                    }).collect(Collectors.toList());
        }
        return eventRoomDataList;
    }
}
