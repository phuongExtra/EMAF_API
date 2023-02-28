package com.emaf.service.repository;

import com.emaf.service.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    //    @Query("SELECT * FROM Room inner join ")
//    List<Room> getAllRoomAvailable(String startTime, String endTime);
    @Query("SELECT e FROM Event e where e.eventRoomSchedules = :room " +
            " AND NOT (:startTime > e.endTime OR :endTime < e.startTime)")
    Room getRoomByIdAndTime(@Param("room") Room room,
                            @Param("startTime") String startTime,
                            @Param("endTime") String endTime);


    @Query(value = "select r.* from em_room r where r.id not in (select e1.room_id from em_event_room_schedule e1 \n" +
            "              left join em_event ee on ee.id = e1.event_id \n" +
            " where   not (:startTime >= ee.end_time or :endTime <= ee.start_time) and ee.status in (:status) )", nativeQuery = true)
    List<Room> getRoomAvailable(@Param("startTime") String startTime,
                                @Param("endTime") String endTime,
                                @Param("status") List<String> status);

    List<Room> findAllByIdIn(List<Long> ids);
}