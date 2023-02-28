package com.emaf.service.repository;

import com.emaf.service.entity.Event;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.enumeration.EEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EventRepository
 *
 * @author: VuongVT2
 * @since: 2022/10/05
 */
@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    boolean existsByHashCheckin(String hash);

    boolean existsByHashCheckout(String hash);

    @Query("select e from Event e where e.status = :status ")
    Page<Event> getTopEventByStatus(@Param("status") EEventStatus status, Pageable pageable);

    @Query(value = "" +
            "FROM Event eme " +
            "WHERE " +
            "   eme.startTime <= :startTime " +
            "AND eme.status = :status")
    List<Event> findAllByStartTimeAndStatus(String startTime, EEventStatus status);

    @Query(value = "" +
            "FROM Event eme " +
            "WHERE " +
            "   eme.endTime <= :endTime " +
            "AND eme.status = :status")
    List<Event> findAllByEndTimeAndStatus(String endTime, EEventStatus status);

    @Query(value = "" +
            "SELECT eme " +
            "FROM " +
            "   Event eme " +
            "WHERE " +
            "   eme.startTime >= :startTime " +
            "   AND eme.endTime <= :endTime ")
    List<Event> findByRangeTime(@Param("startTime") String startTime,
                                @Param("endTime") String endTime);

    @Query(value = "" +
            "SELECT eme " +
            "FROM " +
            "   Event eme " +
            "WHERE " +
            "   eme.startTime >= :startTime " +
            "   AND eme.endTime <= :endTime AND eme.status IN (:status)")
    Page<Event> findByRangeTime(@Param("startTime") String startTime,
                                @Param("endTime") String endTime,
                                @Param("status") List<EEventStatus> status,
                                Pageable pageable);

    @Query("SELECT COUNT(*) " +
            "FROM " +
            "   Event eme " +
            "WHERE " +
            "   eme.startTime >= :startTime " +
            "   AND eme.endTime <= :endTime AND eme.type = :type ")
    int countByRangeTimeAndType(@Param("startTime") String startTime,
                                 @Param("endTime") String endTime,
                                 @Param("type") EEventType type);

    @Query(value = "" +
            "SELECT COUNT(*) " +
            "FROM " +
            "   Event eme " +
            "WHERE " +
            "   eme.startTime >= :startTime " +
            "   AND eme.endTime <= :endTime AND eme.status = :status ")
    int countByRangeTimeAndStatus(@Param("startTime") String startTime,
                                   @Param("endTime") String endTime,
                                   @Param("status") EEventStatus status);

    @Query(value = "" +
            "SELECT COUNT(*) " +
            "FROM " +
            "   Event eme " +
            "WHERE " +
            "   eme.startTime >= :startTime " +
            "   AND eme.endTime <= :endTime  ")
    int countByRangeTime(@Param("startTime") String startTime,
                                  @Param("endTime") String endTime);
}
