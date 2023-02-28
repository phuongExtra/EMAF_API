package com.emaf.service.service;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventParticipation;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.model.event.*;
import org.apache.poi.ss.formula.functions.Even;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * EventService
 *
 * @author: VuongVT2
 * @since: 2022/10/04
 */
public interface EventService {

    Event getEventById(String eventId, String userId);

    String createEvent(String userId, EventCreate eventCreate);

    PagedResponse<Event> filterEvents(String userId, String search, List<String> status, String type, Integer page, Integer size);

    boolean approveEvent(String userId, String eventId);

    boolean rejectEvent(String userId, RejectForm rejectForm);

    PagedResponse<Event> getAllMyEvents(String userId, String search, String status, String type, Integer page, Integer size);

    boolean checkinEvent(String userId, String eventId, String hash);

    boolean checkoutEvent(String userId, String eventId, String hash);

    boolean registerParticipationEvent(String userId, String eventId);

    boolean approveParticipationEvent(String userId, Long eventParticipantId);

    boolean rejectParticipationEvent(String userId, Long eventParticipantId);

    boolean startEvent(String userId, String eventId);

    boolean endEvent(String userId, String eventId);

    String updateBanner(MultipartFile banner, String eventId, String userId);

    void addToFavorite(String eventId, String userId);

    boolean deleteEvent(String userId, String eventId);

    boolean cancelParticipationEvent(String userId, Long eventParticipantId);

    List<DocumentData> uploadDocuments(String userId, DocumentForm documentForm);

    boolean deleteDocument(String userId, Long documentId);

    boolean sendCollaboration(String userId, CollaborationForm collaborationForm);

    PagedResponse<ParticipantReq> getAllEventParticipant(String userId, String eventId, String search, String status, Integer page, Integer size);

    boolean deleteCollaboration(String userId, Long userEventRoleId);

    boolean cancelCollaboration(String userId, Long userEventRoleId);

    boolean stopEvent(String userId, RejectForm stopForm);

    PagedResponse<Event> getAllManagedEvent(String userId, String search, String status, String type, Integer page, Integer size);

    String updateFeedBack(String eventId, String feedbackLink, String userId);

    String updateEvent(String userId, EventCreate eventUpdate);

    DashboardEvent getListEventDashboard(String userId);

    boolean acceptCollaboration(String userId, Long userEventRoleId);

    boolean rejectCollaboration(String userId, Long userEventRoleId);

    boolean updateEventRoom(String userId, EventRoomForm eventRoomForm);

    PagedResponse<Event> getOrganizedEvents(String userId, String search, List<String> status, String type, Integer page, Integer size);

    PagedResponse<Event> getCollaboratingEvents(String userId, String search, List<String> status, String type, Integer page, Integer size);

    PagedResponse<Event> getParticipatingEvents(String userId, String search, List<String> status, String type, Integer page, Integer size);

    void scheduleStartEvent(List<Event> events);

    void scheduleEndEvent(List<Event> eventsEnd);

    List<Event> getFavoriteEvents(String userId);

    List<EventListData> getChartData(String startTime, String endTime);

    PagedResponse<EventListData> getEventReportByRange(String startTime, String endTime, Pageable pageable);
}
