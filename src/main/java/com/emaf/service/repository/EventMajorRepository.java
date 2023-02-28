package com.emaf.service.repository;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventMajor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EventMajorRepository extends JpaRepository<EventMajor, Long>{
    void deleteAllByEvent(Event event);
}