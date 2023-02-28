package com.emaf.service.repository;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTimelineRepository extends JpaRepository<EventTimeline, Long>{
    void deleteAllByEvent(Event event);
}