package com.emaf.service.dao;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventParticipation;
import com.emaf.service.enumeration.EEventParticipationStatus;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.enumeration.EOrganizationCommitteeStatus;
import com.emaf.service.enumeration.ERole;

import java.util.List;

/**
 * EventDAO
 *
 * @author: VuongVT2
 * @since: 2022/10/24
 */
public interface EventDAO {
    List<Event> filterEvent(boolean isManaged, String search, List<EEventStatus> status, String type, long limit, long offset);

    long countFilterEvent(String search, List<EEventStatus> status, String type);

    List<Event> filterAllMyEvents(String userId, String search, String status, String type, long limit, long offset);

    long countAllMyEvent(String userId, String search, String status, String type);

    List<Event> filterAllManagedEvent(String userId, String search, String status, String type, long limit, long offset);

    long countAllManagedEvent(String userId, String search, String status, String type);

    List<Event> filterEventsBy(String userId, ERole role, EOrganizationCommitteeStatus committeeStatus, String search, List<EEventStatus> statuses, String type, long limit, long offset);

    long countfilterEventsBy(String userId, ERole role, EOrganizationCommitteeStatus committeeStatus, String search, List<EEventStatus> statuses, String type);

    List<Event> filterParticipatingEvents(String userId, List<EEventParticipationStatus> participationStatuses ,String search, List<EEventStatus> statuses, String type, long limit, long offset);

    long countParticipatingEvents(String userId, List<EEventParticipationStatus> participationStatuses ,String search, List<EEventStatus> statuses, String type);
}
