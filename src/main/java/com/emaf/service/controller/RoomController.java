package com.emaf.service.controller;

import com.emaf.service.common.constant.AppConstant;
import com.emaf.service.entity.Room;
import com.emaf.service.model.event.EventRoomData;
import com.emaf.service.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RoomController
 *
 * @author: VuongVT2
 * @since: 2022/10/09
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getAllRoom() {
        return roomService.getAllRoom();
    }

    @GetMapping(value = "all-available", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Room> getAllRoomAvailable(@RequestParam(name = "startTime") String startTime,
                                          @RequestParam(name = "endTime") String endTime) {
        return roomService.getAllRoomAvailable(startTime, endTime);
    }

    @GetMapping(value = "event-room-by-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventRoomData> getAllEventRoomBy(@RequestParam(name = "date") String date) {
        return roomService.getAllEventRoomBy(date);
    }

}
