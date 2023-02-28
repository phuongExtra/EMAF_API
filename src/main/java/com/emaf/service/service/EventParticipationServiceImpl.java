package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.entity.EventParticipation;
import com.emaf.service.entity.User;
import com.emaf.service.enumeration.EEventParticipationStatus;
import com.emaf.service.repository.EventParticipationRepository;
import com.emaf.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * EventParticipationServiceImpl
 *
 * @author: PhuongLN
 * @since: 2022/12/15
 */
@Service
public class EventParticipationServiceImpl implements EventParticipationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Message message;

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @Override
    public List<EventParticipation> getParticipationRequests(final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));
        List<EventParticipation> pendingList = eventParticipationRepository.findByUserAndStatus(user,EEventParticipationStatus.NEW);
        List<EventParticipation> rejectedList = eventParticipationRepository.findByUserAndStatus(user,EEventParticipationStatus.REJECTED);
        List<EventParticipation> eventParticipationList = Stream
                .concat(pendingList.stream(), rejectedList.stream())
                .collect(Collectors.toList());
        eventParticipationList = eventParticipationList
                .stream()
                .map(eventParticipation -> {
                    eventParticipation.setEventName(eventParticipation.getEvent().getEventName());
                    eventParticipation.setEventId(eventParticipation.getEvent().getId());
                    return eventParticipation;
                })
                .collect(Collectors.toList());
        return eventParticipationList;
    }
}