package com.emaf.service.controller;

import com.emaf.service.common.constant.AppConstant;
import com.emaf.service.entity.Event;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.model.event.*;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.EventService;
import com.emaf.service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * EventController
 *
 * @author: VuongVT2
 * @since: 2022/09/30
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/event")
public class EventController {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_STUDENT','ROLE_MANAGER')")
    @GetMapping(value = "all/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<Event> filterEvents(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                             @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) List<String> status,
                                             @RequestParam(name = "type", defaultValue = AppConstant.DEFAULT_STR_VALUE) String type,
                                             @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                             @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                             HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.filterEvents(userId, search, status, type, page, size);
    }

    @GetMapping(value = "filterEventParticipant", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<ParticipantReq> getAllEventParticipant(@RequestParam(name = "eventId") String eventId,
                                                                @RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                                @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
                                                                @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                                @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                                HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getAllEventParticipant(userId, eventId, search, status, page, size);
    }

    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping(value = "events-dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    public DashboardEvent getListEventDashboard(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getListEventDashboard(userId);
    }

    @GetMapping(value = "event-detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public Event getEventById(@RequestParam(name = "id") @NotBlank String eventId,
                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getEventById(eventId, userId);
    }

    @GetMapping(value = "update-feedback", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateFeedBack(@RequestParam(name = "eventId") @NotBlank String eventId,
                                 @RequestParam(name = "feedbackLink") @NotBlank String feedbackLink,
                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.updateFeedBack(eventId, feedbackLink, userId);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_STUDENT')")
    @PostMapping(value = "create", produces = MediaType.APPLICATION_JSON_VALUE)
    public String createEvent(@RequestBody @Valid EventCreate eventCreate, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.createEvent(userId, eventCreate);
    }

    @PreAuthorize("hasRole('ROLE_STAFF')")
    @PostMapping(value = "update-event-room", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateEventRoom(@RequestBody @Valid EventRoomForm eventRoomForm, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.updateEventRoom(userId, eventRoomForm);
    }

    @PostMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    public String updateEvent(@RequestBody @Valid EventCreate eventUpdate, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.updateEvent(userId, eventUpdate);
    }


    @PostMapping(value = "send-collaboration", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean sendCollaboration(@RequestBody @Valid CollaborationForm collaborationForm, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.sendCollaboration(userId, collaborationForm);
    }

    @GetMapping(value = "accept-collaboration", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean acceptCollaboration(@RequestParam(name = "userEventRoleId") Long userEventRoleId,
                                       HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.acceptCollaboration(userId, userEventRoleId);
    }

    @GetMapping(value = "reject-collaboration", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean rejectCollaboration(@RequestParam(name = "userEventRoleId") Long userEventRoleId,
                                       HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.rejectCollaboration(userId, userEventRoleId);
    }

    @GetMapping(value = "delete-collaboration", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteCollaboration(@RequestParam(name = "userEventRoleId") Long userEventRoleId,
                                       HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.deleteCollaboration(userId, userEventRoleId);
    }

    @GetMapping(value = "cancel-collaboration", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean cancelCollaboration(@RequestParam(name = "userEventRoleId") Long userEventRoleId,
                                       HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.cancelCollaboration(userId, userEventRoleId);
    }


    @PostMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteEvent(@RequestParam(name = "eventId") String eventId, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.deleteEvent(userId, eventId);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @PostMapping(value = "approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean approveEvent(@RequestParam(name = "eventId") String eventId, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.approveEvent(userId, eventId);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @PostMapping(value = "reject", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean rejectEvent(@Valid @RequestBody RejectForm rejectForm, HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.rejectEvent(userId, rejectForm);
    }

    @GetMapping(value = "my-events", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<Event> getAllMyEvents(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                               @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
                                               @RequestParam(name = "type", defaultValue = AppConstant.DEFAULT_STR_VALUE) String type,
                                               @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                               @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                               HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getAllMyEvents(userId, search, status, type, page, size);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @GetMapping(value = "managed-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<Event> getAllManagedEvent(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                   @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) String status,
                                                   @RequestParam(name = "type", defaultValue = AppConstant.DEFAULT_STR_VALUE) String type,
                                                   @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                   @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                   HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getAllManagedEvent(userId, search, status, type, page, size);
    }

    @GetMapping(value = "organized-events", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<Event> getOrganizedEvents(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                   @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) List<String> status,
                                                   @RequestParam(name = "type", defaultValue = AppConstant.DEFAULT_STR_VALUE) String type,
                                                   @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                   @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                   HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getOrganizedEvents(userId, search, status, type, page, size);
    }

    @GetMapping(value = "collaborating-events", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<Event> getCollaboratingEvents(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                       @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) List<String> status,
                                                       @RequestParam(name = "type", defaultValue = AppConstant.DEFAULT_STR_VALUE) String type,
                                                       @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                       @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                       HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getCollaboratingEvents(userId, search, status, type, page, size);
    }

    @GetMapping(value = "participating-events", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<Event> getParticipatingEvents(@RequestParam(name = "search", defaultValue = AppConstant.DEFAULT_STR_VALUE) String search,
                                                       @RequestParam(name = "status", defaultValue = AppConstant.DEFAULT_STR_VALUE) List<String> status,
                                                       @RequestParam(name = "type", defaultValue = AppConstant.DEFAULT_STR_VALUE) String type,
                                                       @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                       @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                       HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getParticipatingEvents(userId, search, status, type, page, size);
    }

    @GetMapping(value = "checkin", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkinEvent(@RequestParam(name = "eventId") @NotBlank String eventId,
                                @RequestParam(name = "hash") @NotBlank String hash,
                                HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.checkinEvent(userId, eventId, hash);
    }

    @GetMapping(value = "checkout", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkoutEvent(@RequestParam(name = "eventId") @NotBlank String eventId,
                                 @RequestParam(name = "hash") @NotBlank String hash,
                                 HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.checkoutEvent(userId, eventId, hash);
    }

    @GetMapping(value = "register-participation", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean registerParticipationEvent(@RequestParam(name = "eventId") @NotBlank String eventId,
                                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.registerParticipationEvent(userId, eventId);
    }

    @GetMapping(value = "approve-participation", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean approveParticipationEvent(@RequestParam(name = "eventParticipantId") @NotNull Long eventParticipantId,
                                             HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.approveParticipationEvent(userId, eventParticipantId);
    }

    @GetMapping(value = "reject-participation", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean rejectParticipationEvent(@RequestParam(name = "eventParticipantId") @NotNull Long eventParticipantId,
                                            HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.rejectParticipationEvent(userId, eventParticipantId);
    }

    @GetMapping(value = "cancel-participation", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean cancelParticipationEvent(@RequestParam(name = "eventParticipantId") @NotNull Long eventParticipantId,
                                            HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.cancelParticipationEvent(userId, eventParticipantId);
    }

    @GetMapping(value = "start-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean startEvent(@RequestParam(name = "eventId") @NotBlank String eventId,
                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.startEvent(userId, eventId);
    }

    @PostMapping(value = "stop-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean stopEvent(@RequestBody @Valid RejectForm stopForm,
                             HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.stopEvent(userId, stopForm);
    }

    @GetMapping(value = "end-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean endEvent(@RequestParam(name = "eventId") @NotBlank String eventId,
                            HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.endEvent(userId, eventId);
    }

    @PostMapping(value = "/update-banner", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String updateBanner(@RequestParam(value = "banner") MultipartFile banner,
                               @RequestParam(value = "eventId") String eventId,
                               HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.updateBanner(banner, eventId, userId);
    }

    @GetMapping(value = "/add-favorite", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addToFavorite(@RequestParam(value = "eventId") String eventId,
                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        eventService.addToFavorite(eventId, userId);
    }

    @GetMapping(value = "get-favorite-events", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Event> getFavoriteEvents(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.getFavoriteEvents(userId);
    }

    @PostMapping(value = "/upload-document", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<DocumentData> uploadDocuments(@Valid DocumentForm documentForm,
                                              HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.uploadDocuments(userId, documentForm);
    }

    @GetMapping(value = "/delete-document", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteDocument(@RequestParam(value = "documentId") @NotNull Long documentId,
                                  HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventService.deleteDocument(userId, documentId);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @GetMapping(value = "/get-chart-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventListData> getChartData(@RequestParam(value = "startTime") @NotBlank String startTime,
                                            @RequestParam(value = "endTime") @NotBlank String endTime) {
        return eventService.getChartData(startTime, endTime);
    }

    @PreAuthorize("hasAnyRole('ROLE_STAFF','ROLE_MANAGER')")
    @GetMapping(value = "/get-event-report", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<EventListData> getEventReportByRange(@RequestParam(value = "startTime") @NotBlank String startTime,
                                                              @RequestParam(value = "endTime") @NotBlank String endTime,
                                                              @RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                              @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return eventService.getEventReportByRange(startTime, endTime, pageable);
    }
}
