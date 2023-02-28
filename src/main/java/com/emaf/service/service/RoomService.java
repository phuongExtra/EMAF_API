package com.emaf.service.service;

import com.emaf.service.entity.Room;
import com.emaf.service.model.event.EventRoomData;

import java.util.List;

/**
 * RoomService
 *
 * @author: VuongVT2
 * @since: 2022/10/09
 */
public interface RoomService {
    List<Room> getAllRoom();

    List<Room> getAllRoomAvailable(String startTime, String endTime);

    List<EventRoomData> getAllEventRoomBy(String date);
}
