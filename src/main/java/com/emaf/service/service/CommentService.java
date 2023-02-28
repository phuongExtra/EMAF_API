package com.emaf.service.service;

import com.emaf.service.entity.Comment;
import com.emaf.service.model.comment.CommentForm;

import java.util.List;

/**
 * CommentService
 *
 * @author: VuongVT2
 * @since: 2022/10/23
 */
public interface CommentService {
    Comment createComment(CommentForm commentForm);

    boolean updateComment(String userId, CommentForm commentForm);

    Comment deleteComment(String userId, CommentForm commentForm);

    List<Comment> getAllComments(String eventId);
}
