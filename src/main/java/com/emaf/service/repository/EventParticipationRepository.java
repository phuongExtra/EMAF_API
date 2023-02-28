package com.emaf.service.repository;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventParticipation;
import com.emaf.service.entity.User;
import com.emaf.service.enumeration.EEventParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {

    EventParticipation findByEventAndUserAndStatus(Event event, User user, EEventParticipationStatus status);

    EventParticipation findByEventAndUserAndStatusIn(Event event, User user, List<EEventParticipationStatus> statuses);

    void deleteAllByEvent(Event event);

    Optional<EventParticipation> findEventParticipationByUserAndEvent(User user, Event event);

    List<EventParticipation> findAllByEventAndStatusNotIn(Event event, List<EEventParticipationStatus> statuses);

    int countAllByStatus(EEventParticipationStatus status);
    int countAllByStatusIn(List<EEventParticipationStatus> statuses);

    List<EventParticipation> findByUserAndStatus(User user, EEventParticipationStatus statuses);

    List<EventParticipation> findAllByEventAndStatus(Event event, EEventParticipationStatus status);

    int countByEvent(Event event);

    @Query(value = "" +
            "SELECT COUNT(emep.id) " +
            "FROM EventParticipation emep " +
            "WHERE " +
            "   emep.event = :event " +
            "   AND emep.status IN (:statusList) ")
    int countByEventAndStatus(@Param("event") Event event,
                              @Param("statusList") List<EEventParticipationStatus> statusList);

}