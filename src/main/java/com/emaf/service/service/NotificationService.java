package com.emaf.service.service;

import com.emaf.service.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * NotificationService
 *
 * @author: PhuongLN
 * @since: 2022/10/09
 */
@Service
public interface NotificationService {


    List<Notification> getNotifications(String userId);

    void deleteNotification(String notificationId, String userId);
}
