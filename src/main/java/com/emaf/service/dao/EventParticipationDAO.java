package com.emaf.service.dao;

import com.emaf.service.entity.EventParticipation;

import java.util.List;

/**
 * EventParticipationDAO
 *
 * @author: VuongVT2
 * @since: 2022/11/09
 */
public interface EventParticipationDAO {
    List<EventParticipation> filterEventParticipant(String eventId, String search, String status, long limit, long offset);

    long countfilterEventParticipant(String eventId, String search, String status);
}
