package com.emaf.service.repository;

import com.emaf.service.entity.Document;
import com.emaf.service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * DocumentRepository
 *
 * @author: VuongVT2
 * @since: 2022/11/07
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("select d.event.id from Document d where d.id = :documentId")
    String getEventIdBy(@Param("documentId") Long documentId);

    void deleteAllByEvent(Event event);
}
