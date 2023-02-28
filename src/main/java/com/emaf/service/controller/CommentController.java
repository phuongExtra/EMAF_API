package com.emaf.service.controller;

import com.emaf.service.entity.Comment;
import com.emaf.service.model.comment.CommentForm;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * CommentController
 *
 * @author: VuongVT2
 * @since: 2022/10/23
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/comment")
public class CommentController {
    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private SimpMessagingTemplate template;

    @PostMapping(value = "create-comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public void createComment(@RequestBody CommentForm commentForm) {
        Comment comment = commentService.createComment(commentForm);
        template.convertAndSend("/topic/event/" + commentForm.getEventId(), comment);
    }

    @PostMapping(value = "update-comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateComment(@RequestBody CommentForm commentForm,
                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return commentService.updateComment(userId, commentForm);
    }

    @PostMapping(value = "delete-comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteComment(@RequestBody CommentForm commentForm,
                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        Comment comment = commentService.deleteComment(userId, commentForm);
        template.convertAndSend("/topic/event/" + commentForm.getEventId(), comment);
    }

    @GetMapping(value = "all-comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Comment> getAllComments(@RequestParam(name = "eventId") @NotBlank String eventId) {

        return commentService.getAllComments(eventId);
    }
}
