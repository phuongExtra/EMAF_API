package com.emaf.service.controller;

import com.emaf.service.entity.Notification;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.DocumentService;
import com.emaf.service.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * NotificationController
 *
 * @author: PhuongLN
 * @since: 2022/12/12
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AccessTokenService accessTokenService;

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Notification> getNotifications(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return notificationService.getNotifications(userId);
    }

    @GetMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteNotification(@RequestParam(name = "notificationId") String notificationId,
                                   HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        notificationService.deleteNotification(notificationId, userId);
    }
}