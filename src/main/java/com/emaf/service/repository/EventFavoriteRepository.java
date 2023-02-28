package com.emaf.service.repository;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventFavorite;
import com.emaf.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * EventFavoriteRepository
 *
 * @author: PhuongLN
 * @since: 2022/10/31
 */
@Repository
public interface EventFavoriteRepository extends JpaRepository<EventFavorite, Long> {
    Optional<Integer> countAllByEvent(Event event);

    void deleteAllByEvent(Event event);

    boolean existsByEventAndUser(Event event, User user);

    void deleteByEventAndUser(Event event, User user);

    long countAllByEventAndUser(Event event, User user);

    @Query(value = "SELECT ee " +
            "FROM EventFavorite eef " +
            "LEFT JOIN Event ee ON eef.event.id = ee.id " +
            "WHERE eef.user.id = :userId")
    List<Event> getFavoriteList(@Param("userId") String userId);


}
