package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.entity.Notification;
import com.emaf.service.entity.User;
import com.emaf.service.repository.NotificationRepository;
import com.emaf.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * NotificationServiceImpl
 *
 * @author: PhuongLN
 * @since: 2022/10/09
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Message message;

    @Override
    public List<Notification> getNotifications(final String userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));
        List<Notification> notifications = notificationRepository.findByUser(user);
        return notifications;
    }

    @Override
    @Transactional
    public void deleteNotification(final String notificationId, final String userId) {
        User user = userRepository.findById(userId).
                orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));
        notificationRepository.deleteByUserAndId(user, notificationId);
    }
}
