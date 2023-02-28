package com.emaf.service.service;

import com.emaf.service.entity.EventParticipation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * EventParticipationService
 *
 * @author: PhuongLN
 * @since: 2022/12/15
 */
@Service
public interface EventParticipationService {

    List<EventParticipation> getParticipationRequests(String userId);
}