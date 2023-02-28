package com.emaf.service.controller;

import com.emaf.service.entity.Comment;
import com.emaf.service.model.comment.ChatMessage;
import com.emaf.service.model.comment.CommentForm;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocketController
 *
 * @author: VuongVT2
 * @since: 2022/11/27
 */
@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private CommentService commentService;

    @MessageMapping("/event.register")
    public void createComment(@Payload CommentForm commentForm) {
        Comment comment = commentService.createComment(commentForm);
        template.convertAndSend("/topic/event/" + commentForm.getEventId(), comment);
    }

    @MessageMapping("/chat.register")
    @SendTo({"/topic/public"})
    public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }
}
