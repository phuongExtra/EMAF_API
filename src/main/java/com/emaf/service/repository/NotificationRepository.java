package com.emaf.service.repository;

import com.emaf.service.entity.Notification;
import com.emaf.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String>, JpaSpecificationExecutor<Notification> {

    List<Notification> findByUser(User user);

    void deleteByUserAndId(User user, String notificationId);

}