package com.emaf.service.repository;

import com.emaf.service.entity.Comment;
import com.emaf.service.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteAllByParentId(Long parentId);

    List<Comment> getAllByEventAndParentId(Event event, Long parentId);

    List<Comment> getAllByEventAndParentIdOrderByCreatedAtDesc(Event event, Long parentId);

    Optional<Long> countAllByParentId(Long parentId);

    void deleteAllByEvent(Event event);
}