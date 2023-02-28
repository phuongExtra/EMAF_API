package com.emaf.service.repository;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventRoomSchedule;
import com.emaf.service.entity.Room;
import com.emaf.service.enumeration.EEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRoomScheduleRepository extends JpaRepository<EventRoomSchedule, Long>, JpaSpecificationExecutor<EventRoomSchedule> {

    @Query(value = "select count(distinct eers) from em_event_room_schedule eers\n" +
            "        left join em_event ee on ee.id = eers.event_id\n" +
            "where eers.room_id = :roomId AND NOT (:startTime > ee.end_time OR :endTime < ee.start_time) and ee.status = 'APPROVED'", nativeQuery = true)
    Integer checkRoomAvailable(@Param("roomId") Long roomId,
                               @Param("startTime") String startTime,
                               @Param("endTime") String endTime);

    void deleteAllByEvent(Event event);

    @Query("select ers from EventRoomSchedule ers where " +
            "not (:startTime > ers.event.endTime or :endTime < ers.event.startTime) " +
            "and ers.event.status in (:status)")
    List<EventRoomSchedule> getAllByDate(@Param("startTime") String startTime,
                                         @Param("endTime") String endTime,
                                         @Param("status") List<EEventStatus> statuses);

    List<EventRoomSchedule> findAllByEvent(Event event);

}