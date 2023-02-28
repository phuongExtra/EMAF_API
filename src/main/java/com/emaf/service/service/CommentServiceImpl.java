package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.utils.TimeUtils;
import com.emaf.service.entity.Comment;
import com.emaf.service.entity.Event;
import com.emaf.service.entity.OrganizationCommittee;
import com.emaf.service.entity.User;
import com.emaf.service.model.comment.CommentForm;
import com.emaf.service.repository.CommentRepository;
import com.emaf.service.repository.EventRepository;
import com.emaf.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * CommentServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/10/23
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private Message message;


    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Comment createComment(final CommentForm commentForm) {
        User user = userRepository.findById(commentForm.getUserId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));
        Event event = eventRepository.findById(commentForm.getEventId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (commentForm.getParentId() != 0) {
            //check có comment parent không
            commentRepository.findById(commentForm.getParentId())
                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        }
        Comment comment = Comment.builder()
                .createdAt(TimeUtils.comNowDatetime())
                .updatedAt(TimeUtils.comNowDatetime())
                .content(commentForm.getContent())
                .event(event)
                .user(user)
                .parentId(commentForm.getParentId())
                .build();
        commentRepository.save(comment);
        comment.setDelete(false);
        return comment;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean updateComment(final String userId, final CommentForm commentForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));
        Comment comment = commentRepository.findById(commentForm.getCommentId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!user.equals(comment.getUser())) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        comment.setContent(commentForm.getContent());
        comment.setUpdatedAt(TimeUtils.comNowDatetime());
        commentRepository.save(comment);
        comment.setDelete(false);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Comment deleteComment(final String userId, final CommentForm commentForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));
        Comment comment = commentRepository.findById(commentForm.getCommentId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        // user quản lý sự kiện
        OrganizationCommittee userEvent = comment.getEvent().getOrganizationCommittees()
                .stream().filter(eventUser -> eventUser.getUser().equals(user)).findFirst().orElse(null);
        if (!user.equals(comment.getUser()) && Objects.isNull(userEvent)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        if (comment.getParentId() == 0) {
            commentRepository.deleteAllByParentId(comment.getId());
        }
        commentRepository.delete(comment);
        comment.setDelete(true);
        return comment;
    }

    @Override
    public List<Comment> getAllComments(final String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " EVENT"));
        List<Comment> comments = commentRepository.getAllByEventAndParentIdOrderByCreatedAtDesc(event, (long) 0);
        comments.forEach(comment -> {
            comment.setTotalReply(commentRepository.countAllByParentId(comment.getId()).orElse((long) 0));
            comment.setChildrenComments(commentRepository.getAllByEventAndParentId(event, comment.getId()));
        });
        return comments;
    }

}
