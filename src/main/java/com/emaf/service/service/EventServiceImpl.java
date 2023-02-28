package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ExistenceException;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.logging.AppLogger;
import com.emaf.service.common.utils.DataBuilder;
import com.emaf.service.common.utils.IDGenerator;
import com.emaf.service.common.utils.TimeUtils;
import com.emaf.service.component.S3Component;
import com.emaf.service.dao.EventDAO;
import com.emaf.service.dao.EventParticipationDAO;
import com.emaf.service.entity.*;
import com.emaf.service.enumeration.*;
import com.emaf.service.model.common.CommonData;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.model.event.*;
import com.emaf.service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EventServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/10/04
 */
@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventUserRepository eventUserRepository;

    @Autowired
    private EventTimelineRepository eventTimelineRepository;

    @Autowired
    private EventEquipmentRepository eventEquipmentRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @Autowired
    private OrganizationCommitteeRepository organizationCommitteeRepository;

    @Autowired
    private EventRoomScheduleRepository eventRoomScheduleRepository;

    @Autowired
    private EventFavoriteRepository eventFavoriteRepository;

    @Autowired
    private EventMajorRepository eventMajorRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private S3Component s3Component;

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private EventParticipationDAO eventParticipationDAO;

    @Autowired
    private Message message;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Event getEventById(String eventId, String userId) {
        Event event = eventRepository.findById(eventId)
                .orElse(null);
        Integer numberOfFavorites = eventFavoriteRepository.countAllByEvent(event)
                .orElse(0);
        assert event != null;
        event.setNumberOfFavorites(numberOfFavorites);
        User user = userRepository.findById(userId)
                .orElse(null);
        boolean isFavorited = eventFavoriteRepository.existsByEventAndUser(event, user);
        event.setFavorited(isFavorited);
        int numberExpected = event.getMaxNumOfParticipant() + event.getParticipantDeviation();
        int numberRegis = eventParticipationRepository.countAllByStatus(EEventParticipationStatus.APPROVED);
        event.setParticipationLimit(numberRegis >= numberExpected);

        event.setNumOfParticipationRegis(numberRegis);

        EventParticipation eventParticipation = eventParticipationRepository
                .findEventParticipationByUserAndEvent(user, event).orElse(null);
        event.setEventParticipation(eventParticipation);
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.nonNull(organizationCommittee)) {
            event.setRoleInEvent(organizationCommittee.getRole().getId().name());
        }
        return event;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public String createEvent(final String userId, final EventCreate eventCreate) {
        User organizer = userRepository.getById(userId);
        String id = IDGenerator.generateID(eventRepository, 10);
        Event event = DataBuilder.to(eventCreate, Event.class);
        event.setId(id);
        event.setType(EEventType.valueOf(eventCreate.getType()));
        event.setCreatedAt(TimeUtils.comNowDatetime());
        event.setUpdatedAt(TimeUtils.comNowDatetime());
        event.setLocation(eventCreate.getLocation());
        event.setNote(eventCreate.getNote());
        event.setFeedbackLink(eventCreate.getFeedbackLink());
        event.setStatus(EEventStatus.NEW);
        event.setApprovalRequired(eventCreate.isApprovalRequired());
        event.setMinNumOfParticipant(eventCreate.getMinNumOfParticipant());
        event.setMaxNumOfParticipant(eventCreate.getMaxNumOfParticipant());
        event.setParticipantDeviation(eventCreate.getParticipantDeviation());
        event.setCheckinRequired(ECheckinRequired.valueOf(eventCreate.getCheckinRequired()));
        if (StringUtils.hasLength(eventCreate.getRegisterStartTime())) {
            event.setRegistrationStartTime(eventCreate.getRegisterStartTime());
        }
        if (StringUtils.hasLength(eventCreate.getRegisterEndTime())) {
            event.setRegisterEndTime(eventCreate.getRegisterEndTime());
        }

        if (organizer.getRole().getId().equals(ERole.ROLE_STAFF)) {
            event.setStatus(EEventStatus.PENDING);
            event.setCreatedBy(userId);
        }

        if (StringUtils.hasLength(eventCreate.getSpeakers())) {
            event.setSpeakers(eventCreate.getSpeakers());
        }
        if (StringUtils.hasLength(eventCreate.getMasterOfCeremonies())) {
            event.setMasterOfCeremonies(eventCreate.getMasterOfCeremonies());
        }
        eventRepository.save(event);

        if (Objects.nonNull(eventCreate.getRoomIds())) {
            List<Room> rooms = roomRepository.findAllByIdIn(eventCreate.getRoomIds());
            if (!CollectionUtils.isEmpty(rooms)) {
                for (Room room : rooms) {
                    //check room đã có người dùng chưa
                    Integer roomCheck = eventRoomScheduleRepository.checkRoomAvailable(room.getId(), eventCreate.getStartTime(), eventCreate.getEndTime());
                    if (roomCheck > 0) {
                        throw new ExistenceException(message.getDuplicateData() + " Room Id: " + room.getId() + " đã được đặt!");
                    }
                    EventRoomSchedule eventRoomSchedule = EventRoomSchedule.builder()
                            .event(event)
                            .room(room)
                            .build();
                    eventRoomScheduleRepository.save(eventRoomSchedule);
                }
            }
        }

        OrganizationCommittee organizationCommittee = OrganizationCommittee.builder()
                .event(event)
                .role(roleRepository.getById(ERole.ROLE_ORGANIZER))
                .status(EOrganizationCommitteeStatus.ACCEPTED)
                .user(organizer)
                .build();
        eventUserRepository.saveAndFlush(organizationCommittee);

        List<EventUserForm> eventUserFormList = eventCreate.getEventUsers();
        if (!CollectionUtils.isEmpty(eventUserFormList)) {
            eventUserFormList.forEach(eventUserForm -> {
                OrganizationCommittee organizationCommitteeR = OrganizationCommittee.builder()
                        .event(event)
                        .role(roleRepository.getById(ERole.valueOf(eventUserForm.getRoleId())))
                        .status(EOrganizationCommitteeStatus.PENDING)
                        .user(userRepository.findById(eventUserForm.getUserId())
                                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                        .build();
                eventUserRepository.saveAndFlush(organizationCommitteeR);
            });
        }

        List<EventEquipmentForm> eventEquipmentForms = eventCreate.getEventEquipments();
        if (!CollectionUtils.isEmpty(eventEquipmentForms)) {
            eventEquipmentForms.forEach(eventEquipmentForm -> {
                //check số lượng mượn phải < số lượng equipment còn lại
                if (eventEquipmentForm.getQuantity() > equipmentService.numberOfEquipmentAvailable(eventEquipmentForm.getEquipmentId(), eventEquipmentForm.getBorrowTime(), eventEquipmentForm.getReturnTime())) {
                    throw new ServerErrorException("Insufficient number of equipment - equipmentId: " + eventEquipmentForm.getEquipmentId());
                }

                EventEquipment eventEquipment = EventEquipment.builder()
                        .quantity(eventEquipmentForm.getQuantity())
                        .event(event)
                        .borrowTime(eventEquipmentForm.getBorrowTime())
                        .returnTime(eventEquipmentForm.getReturnTime())
                        .status(EventEquipmentStatus.PENDING)
                        .equipment(equipmentRepository.findById(eventEquipmentForm.getEquipmentId())
                                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                        .build();
                if (ERole.ROLE_STAFF.equals(organizer.getRole().getId()) || ERole.ROLE_MANAGER.equals(organizer.getRole().getId())) {
                    eventEquipment.setStatus(EventEquipmentStatus.APPROVED);
                }
                eventEquipmentRepository.saveAndFlush(eventEquipment);
            });
        }


        List<EventTimelineCreate> eventTimelineCreates = eventCreate.getEventTimelines();
        if (!CollectionUtils.isEmpty(eventTimelineCreates)) {
            eventTimelineCreates.forEach(eventTimelineCreate -> {
                EventTimeline eventTimeline = DataBuilder.to(eventTimelineCreate, EventTimeline.class);
                eventTimeline.setEvent(event);
                eventTimelineRepository.saveAndFlush(eventTimeline);
            });
        }

        return event.getId();
    }

    @Override
    public PagedResponse<Event> filterEvents(final String userId, final String search, final List<String> status, final String type, final Integer page, final Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User Id null"));
        boolean isManaged = false;
        if (ERole.ROLE_STAFF.equals(user.getRole().getId()) ||
                ERole.ROLE_MANAGER.equals(user.getRole().getId())) {
            isManaged = true;

        }
        List<EEventStatus> statuses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(status)) {
            statuses = status.stream().map(EEventStatus::valueOf).collect(Collectors.toList());
        }
        List<Event> events = eventDAO.filterEvent(isManaged, search, statuses, type, size, (page - 1) * size);
        long totalElements = eventDAO.countFilterEvent(search, statuses, type);

        if (!CollectionUtils.isEmpty(events)) {
            for (Event event : events) {
                OrganizationCommittee organizationCommittee = event.getOrganizationCommittees().stream()
                        .filter(p -> p.getRole().getId().equals(ERole.ROLE_ORGANIZER)).findFirst().orElse(null);

                if (Objects.nonNull(organizationCommittee) && Objects.nonNull(organizationCommittee.getUser())) {
                    user = organizationCommittee.getUser();
                    event.setRequestedBy(user.getLastName() + " " + user.getFirstName());
                }
            }
        }
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        return new PagedResponse<>(events, page, size, totalElements, totalPages);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean approveEvent(final String userId, final String eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (ERole.ROLE_STAFF.name().equals(user.getRole().getId().name()) &&
                event.getStatus().equals(EEventStatus.NEW)) {
            event.setStatus(EEventStatus.PENDING);
        } else if (ERole.ROLE_MANAGER.name().equals(user.getRole().getId().name()) &&
                event.getStatus().equals(EEventStatus.PENDING)) {
            event.setStatus(EEventStatus.APPROVED);
        } else {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Sai status event");
        }
        if (StringUtils.hasLength(event.getCreatedBy())) {
            event.setCreatedBy(userId);
        }
        event.setUpdatedAt(TimeUtils.comNowDatetime());
        event.setHandleBy(userId);
        String hashCheckout;
        do {
            hashCheckout = UUID.randomUUID().toString();
        } while (eventRepository.existsByHashCheckout(hashCheckout));
        event.setHashCheckout(hashCheckout);
        String hashCheckin;
        do {
            hashCheckin = UUID.randomUUID().toString();
        } while (eventRepository.existsByHashCheckin(hashCheckin));
        event.setHashCheckin(hashCheckin);

        eventRepository.save(event);

        String organizerId = null;
        for (OrganizationCommittee organizationCommittee : event.getOrganizationCommittees()) {

            if (ERole.ROLE_ORGANIZER.name().equals(organizationCommittee.getRole().getId().name())) {
                organizerId = organizationCommittee.getUser().getId();
            }
        }

        User organizer = userRepository.findById(organizerId).
                orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));

        String id = IDGenerator.generateID(notificationRepository, 10);
        if (ERole.ROLE_STAFF.name().equals(user.getRole().getId().name())) {
            Notification notification = Notification
                    .builder()
                    .id(id)
                    .user(organizer)
                    .type("Approve event request ")
                    .content("Event: " + event.getEventName() + " has been approved by the staff!")
                    .targetUrl(event.getId())
                    .build();
            notificationRepository.saveAndFlush(notification);
            template.convertAndSend("/topic/notification/" + organizerId, notification);
        } else if (ERole.ROLE_MANAGER.name().equals(user.getRole().getId().name())) {
            Notification notification = Notification
                    .builder()
                    .id(id)
                    .user(organizer)
                    .type("Approve event request ")
                    .content("Event: " + event.getEventName() + " has been approved by the manager and will be public to other students!")
                    .targetUrl(event.getId())
                    .build();
            notificationRepository.saveAndFlush(notification);
            template.convertAndSend("/topic/notification/" + organizerId, notification);
        }

        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean rejectEvent(final String userId, final RejectForm rejectForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        Event event = eventRepository.findById(rejectForm.getEventId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        event.setStatus(EEventStatus.REJECTED);
        if (user.getRole().getId().equals(ERole.ROLE_STAFF)) {
            event.setStaffFeedback(rejectForm.getFeedBack());
        }
        if (user.getRole().getId().equals(ERole.ROLE_MANAGER)) {
            event.setManagerFeedback(rejectForm.getFeedBack());
        }
        eventRoomScheduleRepository.deleteAllByEvent(event);
        event.setUpdatedAt(TimeUtils.comNowDatetime());
        event.setHandleBy(userId);
        eventRepository.save(event);

        String organizerId = null;
        for (OrganizationCommittee organizationCommittee : event.getOrganizationCommittees()) {

            if (ERole.ROLE_ORGANIZER.name().equals(organizationCommittee.getRole().getId().name())) {
                organizerId = organizationCommittee.getUser().getId();
            }
        }
        ;

        User organizer = userRepository.findById(organizerId).
                orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));

        String id = IDGenerator.generateID(notificationRepository, 10);
        Notification notification = Notification
                .builder()
                .id(id)
                .user(organizer)
                .type("Reject event request ")
                .content("Event: " + event.getEventName() + " has been rejected!")
                .targetUrl(event.getId())
                .build();
        notificationRepository.saveAndFlush(notification);
        template.convertAndSend("/topic/notification/" + organizerId, notification);

        return true;
    }

    @Override
    public PagedResponse<Event> getAllMyEvents(final String userId, final String search, final String status, final String type, final Integer page, final Integer size) {
//        Role role = roleRepository.findById(ERole.valueOf(roleId))
//                .orElseThrow(() -> new ServerErrorException(message.getErrorUnauthorized()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getErrorUnauthorized()));

        long totalElements;
        List<Event> events;
        events = eventDAO.filterAllMyEvents(userId, search, status, type, size, (page - 1) * size);
        totalElements = eventDAO.countAllMyEvent(userId, search, status, type);
        if (!CollectionUtils.isEmpty(events)) {
            for (Event event : events) {
                OrganizationCommittee organizationCommittee = event.getOrganizationCommittees().stream()
                        .filter(p -> p.getRole().getId().equals(ERole.ROLE_ORGANIZER)).findFirst().orElse(null);
                assert organizationCommittee != null;
                User userR = organizationCommittee.getUser();
                event.setRequestedBy(userR.getLastName() + " " + userR.getFirstName());
            }
        }
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        return new PagedResponse<>(events, page, size, totalElements, totalPages);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean checkinEvent(final String userId, final String eventId, final String hash) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!hash.equals(event.getHashCheckin())) {
            throw new ServerErrorException("mã QR không đúng!");
        }
        EventParticipation eventParticipation = eventParticipationRepository.findByEventAndUserAndStatus(event, user, EEventParticipationStatus.APPROVED);
        if (Objects.isNull(eventParticipation)) {
            throw new ServerErrorException("User chưa đăng ký tham gia event");
        }
        eventParticipation.setCheckinTime(TimeUtils.comNowDatetime());
        eventParticipation.setStatus(EEventParticipationStatus.CHECKED_IN);
        eventParticipationRepository.save(eventParticipation);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean checkoutEvent(final String userId, final String eventId, final String hash) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!hash.equals(event.getHashCheckout())) {
            throw new ServerErrorException(" mã QR không đúng!");
        }
        EventParticipation eventParticipation = eventParticipationRepository.findByEventAndUserAndStatus(event, user, EEventParticipationStatus.CHECKED_IN);
        if (Objects.isNull(eventParticipation)) {
            throw new ServerErrorException(" User chưa đăng ký tham gia event");
        }
        if (Objects.isNull(eventParticipation.getCheckinTime())) {
            throw new ServerErrorException(" User chưa checkin sự kiện");
        }
        eventParticipation.setCheckoutTime(TimeUtils.comNowDatetime());
        eventParticipation.setStatus(EEventParticipationStatus.CHECKED_OUT);
        eventParticipationRepository.saveAndFlush(eventParticipation);
        return true;

    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean registerParticipationEvent(final String userId, final String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " Event null"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));
        if (!event.getStatus().equals(EEventStatus.APPROVED)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Event chưa đưuọc đăng ký");
        }
        int numberExpected = event.getMaxNumOfParticipant() + event.getParticipantDeviation();
        int numberRegis = eventParticipationRepository.countAllByStatus(EEventParticipationStatus.APPROVED);
        if (numberRegis > numberExpected) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Số lượng người tham gia đã đủ");
        }


        EventParticipation eventParticipation = EventParticipation.builder()
                .event(event)
                .status(EEventParticipationStatus.NEW)
                .user(user)
                .build();
        if (!event.isApprovalRequired()) {
            eventParticipation.setStatus(EEventParticipationStatus.APPROVED);
        }
        eventParticipationRepository.save(eventParticipation);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean approveParticipationEvent(final String userId, final Long eventParticipantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));
        EventParticipation eventParticipation = eventParticipationRepository.findById(eventParticipantId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " EventParticipant null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(eventParticipation.getEvent(), user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + "Không có quyền chủ event");
        }
        eventParticipation.setStatus(EEventParticipationStatus.APPROVED);
        eventParticipationRepository.save(eventParticipation);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean rejectParticipationEvent(final String userId, final Long eventParticipantId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));
        EventParticipation eventParticipation = eventParticipationRepository.findById(eventParticipantId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " EventParticipant null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(eventParticipation.getEvent(), user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + "Không có quyền chủ event");
        }
        eventParticipation.setStatus(EEventParticipationStatus.REJECTED);
        eventParticipationRepository.save(eventParticipation);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean startEvent(final String userId, final String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " Event null"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));

        int minNumberParticipant = event.getMinNumOfParticipant();
        int numberRegis = eventParticipationRepository.countAllByStatusIn(Arrays.asList(EEventParticipationStatus.APPROVED,EEventParticipationStatus.CHECKED_IN));
        if (minNumberParticipant > numberRegis) {
            event.setStatus(EEventStatus.STOPPED);
            event.setStaffFeedback("Không đủ số lượng người tham gia!");
            eventRepository.save(event);
            List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEventAndStatus(event, EEventParticipationStatus.APPROVED);
            String notiContent = event.getEventName() + " has stopped!";
            if (!CollectionUtils.isEmpty(eventParticipations)) {
                for (EventParticipation eventParticipation : eventParticipations) {
                    String id = IDGenerator.generateID(notificationRepository, 10);
                    User participation = eventParticipation.getUser();
                    Notification notification = Notification
                            .builder()
                            .id(id)
                            .user(participation)
                            .content(notiContent)
                            .type("Stop Event")
                            .targetUrl(eventId)
                            .build();
                    notificationRepository.saveAndFlush(notification);
                    template.convertAndSend("/topic/notification/" + participation.getId(), notification);
                }
            }
            return true;
//            throw new ServerErrorException(message.getErrorUnauthorized() + " Không đủ số lượng tối thiểu người đăng ký tham gia!");
        }

        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + "Không có quyền chủ event");
        }
//        if (StringUtils.hasLength(event.getHashCheckin())) {
//            return event.getHashCheckin();
//        }
//        String uuid;
//        do {
//            uuid = UUID.randomUUID().toString();
//        } while (eventRepository.existsByHashCheckin(uuid));
//        event.setHashCheckin(uuid);
        event.setStatus(EEventStatus.RUNNING);
        eventRepository.save(event);
        List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEventAndStatus(event, EEventParticipationStatus.APPROVED);
        String notiContent = event.getEventName() + " has started!";
        if (!CollectionUtils.isEmpty(eventParticipations)) {
            for (EventParticipation eventParticipation : eventParticipations) {
                String id = IDGenerator.generateID(notificationRepository, 10);
                User participation = eventParticipation.getUser();
                Notification notification = Notification
                        .builder()
                        .id(id)
                        .user(participation)
                        .content(notiContent)
                        .type("Start Event")
                        .targetUrl(eventId)
                        .build();
                notificationRepository.saveAndFlush(notification);
                template.convertAndSend("/topic/notification/" + participation.getId(), notification);
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void scheduleStartEvent(List<Event> events) {
        if (!CollectionUtils.isEmpty(events)) {
            for (Event event : events) {
                int minNumberParticipant = event.getMinNumOfParticipant();
                int numberRegis = eventParticipationRepository.countAllByStatusIn(Arrays.asList(EEventParticipationStatus.APPROVED,EEventParticipationStatus.CHECKED_IN));
                if (minNumberParticipant <= numberRegis) {
//                    String uuid;
//                    do {
//                        uuid = UUID.randomUUID().toString();
//                    } while (eventRepository.existsByHashCheckin(uuid));
//                    event.setHashCheckin(uuid);
                    event.setStatus(EEventStatus.RUNNING);
                    eventRepository.saveAndFlush(event);
                    List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEventAndStatus(event, EEventParticipationStatus.APPROVED);
                    String notiContent = event.getEventName() + " has started!";
                    if (!CollectionUtils.isEmpty(eventParticipations)) {
                        for (EventParticipation eventParticipation : eventParticipations) {
                            String id = IDGenerator.generateID(notificationRepository, 10);
                            User participation = eventParticipation.getUser();
                            Notification notification = Notification
                                    .builder()
                                    .id(id)
                                    .user(participation)
                                    .type("Start Event")
                                    .content(notiContent)
                                    .targetUrl(event.getId())
                                    .build();
                            notificationRepository.saveAndFlush(notification);
                            template.convertAndSend("/topic/notification/" + participation.getId(), notification);
                        }
                    }
                } else {
                    event.setStatus(EEventStatus.STOPPED);
                    event.setStaffFeedback("Không đủ số lượng người tham gia!");
                    eventRepository.saveAndFlush(event);
                    List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEventAndStatus(event, EEventParticipationStatus.APPROVED);
                    String notiContent = event.getEventName() + " has stopped!";
                    if (!CollectionUtils.isEmpty(eventParticipations)) {
                        for (EventParticipation eventParticipation : eventParticipations) {
                            String id = IDGenerator.generateID(notificationRepository, 10);
                            User participation = eventParticipation.getUser();
                            Notification notification = Notification
                                    .builder()
                                    .id(id)
                                    .user(participation)
                                    .type("Stop Event")
                                    .content(notiContent)
                                    .targetUrl(event.getId())
                                    .build();
                            notificationRepository.saveAndFlush(notification);
                            template.convertAndSend("/topic/notification/" + participation.getId(), notification);
                        }
                    }
                }
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void scheduleEndEvent(List<Event> eventsEnd) {
        if (!CollectionUtils.isEmpty(eventsEnd)) {
            eventsEnd = eventsEnd.stream()
                    .map(event -> {
//                        if (ECheckinRequired.BOTH.equals(event.getCheckinRequired())) {
//                            String uuid;
//                            do {
//                                uuid = UUID.randomUUID().toString();
//                            } while (eventRepository.existsByHashCheckout(uuid));
//                            event.setHashCheckout(uuid);
//                        }
                        event.setStatus(EEventStatus.FINISHED);
                        return event;
                    }).collect(Collectors.toList());
            eventRepository.saveAll(eventsEnd);
        }
    }

    @Override
    public List<Event> getFavoriteEvents(final String userId) {
        List<Event> favoriteList = eventFavoriteRepository.getFavoriteList(userId);
        return favoriteList;
    }

    @Override
    public List<EventListData> getChartData(final String startTime, final String endTime) {
        Role role = roleRepository.findById(ERole.ROLE_ORGANIZER)
                .orElseThrow(() -> new ExistenceException());

        return eventRepository.findByRangeTime(startTime, endTime)
                .stream()
                .map(item -> {
                    ECheckinRequired checkinRequired = item.getCheckinRequired();
                    EventListData eventList = DataBuilder.to(item, EventListData.class);
                    List<CommonData> roomList = new ArrayList<>();
                    List<EventRoomSchedule> eventRoomScheduleList = eventRoomScheduleRepository.findAllByEvent(item);

//                    if(Objects.nonNull(eventRoomSchedule) && Objects.nonNull(eventRoomSchedule.getRoom())){
//                        CommonData room = CommonData.builder()
//                                .id(String.valueOf(eventRoomSchedule.getRoom().getId()))
//                                .name(eventRoomSchedule.getRoom().getRoomName())
//                                .build();
//                        eventList.setRoom(room);
//                    }

                    if (!CollectionUtils.isEmpty(eventRoomScheduleList)) {
                        for (EventRoomSchedule eventRoomSchedule : eventRoomScheduleList) {
                            if (Objects.nonNull(eventRoomSchedule.getRoom())) {
                                CommonData room = CommonData.builder()
                                        .id(String.valueOf(eventRoomSchedule.getRoom().getId()))
                                        .name(eventRoomSchedule.getRoom().getRoomName())
                                        .build();
                                roomList.add(room);
                            }
                        }
                    }
                    eventList.setRoom(roomList);

                    OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndRole(item, role)
                            .orElse(null);
                    String organizerName = "";
                    if (Objects.nonNull(organizationCommittee)) {
                        User organizer = organizationCommittee.getUser();
                        organizerName = organizer.getLastName() + " " + organizer.getFirstName();
                    }

                    int actualNumOfParticipant;
                    if (ECheckinRequired.BOTH.equals(checkinRequired) || ECheckinRequired.CHECKIN.equals(checkinRequired)) {
                        actualNumOfParticipant = eventParticipationRepository.countByEventAndStatus(item,
                                Arrays.asList(EEventParticipationStatus.CHECKED_IN, EEventParticipationStatus.CHECKED_OUT));
                    } else actualNumOfParticipant = eventParticipationRepository.countByEvent(item);

                    eventList.setOrganizerName(organizerName);
                    eventList.setActualNumOfParticipant(actualNumOfParticipant);

                    return eventList;
                }).collect(Collectors.toList());
    }

    @Override
    public PagedResponse<EventListData> getEventReportByRange(final String startTime, final String endTime, final Pageable pageable) {
        Role role = roleRepository.findById(ERole.ROLE_ORGANIZER)
                .orElseThrow(() -> new ExistenceException());

        Page<Event> events = eventRepository.findByRangeTime(startTime, endTime, Arrays.asList(EEventStatus.FINISHED, EEventStatus.STOPPED), pageable);
        List<EventListData> eventListData = events.getContent()
                .stream()
                .map(item -> {
                    ECheckinRequired checkinRequired = item.getCheckinRequired();
                    EventListData eventList = DataBuilder.to(item, EventListData.class);
                    List<CommonData> roomList = new ArrayList<>();
                    List<EventRoomSchedule> eventRoomScheduleList = eventRoomScheduleRepository.findAllByEvent(item);

//                    if(Objects.nonNull(eventRoomSchedule) && Objects.nonNull(eventRoomSchedule.getRoom())){
//                        CommonData room = CommonData.builder()
//                                .id(String.valueOf(eventRoomSchedule.getRoom().getId()))
//                                .name(eventRoomSchedule.getRoom().getRoomName())
//                                .build();
//                        eventList.setRoom(room);
//                    }

                    if (!CollectionUtils.isEmpty(eventRoomScheduleList)) {
                        for (EventRoomSchedule eventRoomSchedule : eventRoomScheduleList) {
                            if (Objects.nonNull(eventRoomSchedule.getRoom())) {
                                CommonData room = CommonData.builder()
                                        .id(String.valueOf(eventRoomSchedule.getRoom().getId()))
                                        .name(eventRoomSchedule.getRoom().getRoomName())
                                        .build();
                                roomList.add(room);
                            }
                        }
                    }
                    eventList.setRoom(roomList);

                    OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndRole(item, role)
                            .orElse(null);
                    String organizerName = "";
                    if (Objects.nonNull(organizationCommittee)) {
                        User organizer = organizationCommittee.getUser();
                        organizerName = organizer.getLastName() + " " + organizer.getFirstName();
                    }

                    int actualNumOfParticipant;
                    if (ECheckinRequired.BOTH.equals(checkinRequired) || ECheckinRequired.CHECKIN.equals(checkinRequired)) {
                        actualNumOfParticipant = eventParticipationRepository.countByEventAndStatus(item,
                                Arrays.asList(EEventParticipationStatus.CHECKED_IN, EEventParticipationStatus.CHECKED_OUT));
                    } else actualNumOfParticipant = eventParticipationRepository.countByEvent(item);

                    eventList.setOrganizerName(organizerName);
                    eventList.setActualNumOfParticipant(actualNumOfParticipant);

                    return eventList;
                }).collect(Collectors.toList());

        return new PagedResponse<>(eventListData, events.getNumber(), events.getSize(), events.getTotalElements(), events.getTotalPages());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean endEvent(final String userId, final String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " Event null"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));

        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Không có quyền chủ event");
        }
//        String uuid = "";
//        if (ECheckinRequired.BOTH.equals(event.getCheckinRequired())) {
//            do {
//                uuid = UUID.randomUUID().toString();
//            } while (eventRepository.existsByHashCheckout(uuid));
//            event.setHashCheckout(uuid);
//        }
        event.setStatus(EEventStatus.FINISHED);
        eventRepository.save(event);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public String updateBanner(final MultipartFile banner, final String eventId, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData() + " User null"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData() + " Event null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Không có quyền update banner!");
        }

        try {
            if (StringUtils.hasLength(event.getBanner())) {
                String fileName = event.getBanner().substring(event.getBanner().lastIndexOf("/") + 1);
                s3Component.delete("events/" + eventId + "/banner", fileName);
            }
            String bannerEvent = s3Component.upload("events/" + eventId + "/banner", banner);
            event.setBanner(bannerEvent);
            eventRepository.save(event);
            return bannerEvent;
        } catch (IOException | URISyntaxException e) {
            AppLogger.errorLog(e.getMessage(), e);
            throw new ServerErrorException(message.getErrorUploadFileError());
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void addToFavorite(final String eventId, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData() + "User is null"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ExistenceException(message.getWarnNoData() + " Event is null"));

        if (!eventFavoriteRepository.existsByEventAndUser(event, user)) {
            EventFavorite eventFavorite = new EventFavorite();
            eventFavorite.setUser(user);
            eventFavorite.setEvent(event);
            eventFavoriteRepository.save(eventFavorite);
        } else {
            eventFavoriteRepository.deleteByEventAndUser(event, user);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean deleteEvent(final String userId, final String eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " event null"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " user null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getWarnNoData() + " user event role null");
        }
        if (!organizationCommittee.getRole().getId().equals(ERole.ROLE_ORGANIZER)
                && !user.getRole().getId().equals(ERole.ROLE_ADMIN)
                && !user.getRole().getId().equals(ERole.ROLE_STAFF)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Không có quyền!");
        }
        organizationCommitteeRepository.deleteAllByEvent(event);
        eventTimelineRepository.deleteAllByEvent(event);
        eventRoomScheduleRepository.deleteAllByEvent(event);
        eventParticipationRepository.deleteAllByEvent(event);
        eventMajorRepository.deleteAllByEvent(event);
        eventFavoriteRepository.deleteAllByEvent(event);
        eventEquipmentRepository.deleteAllByEvent(event);
        documentRepository.deleteAllByEvent(event);
        commentRepository.deleteAllByEvent(event);
        eventRepository.delete(event);

        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean cancelParticipationEvent(final String userId, final Long eventParticipantId) {
        EventParticipation eventParticipation = eventParticipationRepository.findById(eventParticipantId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData()));
        if (!userId.equals(eventParticipation.getUser().getId())) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Không có quyền");
        }
        if (eventParticipation.getEvent().getStatus().equals(EEventStatus.RUNNING)) {
            throw new ServerErrorException(message.getErrorBadRequest() + " Sự kiện đã bắt đầu, không được hủy");
        }
        eventParticipationRepository.delete(eventParticipation);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public List<DocumentData> uploadDocuments(final String userId, final DocumentForm documentForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " user null"));
        Event event = eventRepository.findById(documentForm.getEventId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " event null"));
        if (!organizationCommitteeRepository.existsByUserAndEventAndStatus(user, event, EOrganizationCommitteeStatus.ACCEPTED)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " không có quyền upload");
        }

        MultipartFile[] documentFiles = documentForm.getDocuments();
        List<Document> documents = new ArrayList<>();
        if (Objects.nonNull(documentFiles) && documentFiles.length != 0) {
            try {
                for (var i = 0; i < documentFiles.length; i++) {
                    String link = s3Component.upload("events/" + documentForm.getEventId() + "/documents", documentFiles[i]);
                    Document document = Document.builder()
                            .targetUrl(link)
                            .name(documentFiles[i].getOriginalFilename())
                            .event(event)
                            .build();
                    documents.add(document);
                }
            } catch (IOException | URISyntaxException e) {
                AppLogger.errorLog(e.getMessage(), e);
                throw new ServerErrorException(message.getErrorUploadFileError());
            }
            documentRepository.saveAllAndFlush(documents);
        }

        List<DocumentData> documentDataList = DataBuilder.toList(documents, DocumentData.class);
        return documentDataList;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean deleteDocument(final String userId, final Long documentId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " user null"));

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " document null"));

        String eventId = documentRepository.getEventIdBy(documentId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " event null"));
        if (!organizationCommitteeRepository.existsByUserAndEventAndStatus(user, event, EOrganizationCommitteeStatus.ACCEPTED)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Không có quyền xóa");
        }
        String fileName = document.getTargetUrl().substring(document.getTargetUrl().lastIndexOf("/") + 1);
        s3Component.delete("events/" + eventId + "/documents", fileName);
        documentRepository.delete(document);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean sendCollaboration(final String userId, final CollaborationForm collaborationForm) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " user null"));
        Event event = eventRepository.findById(collaborationForm.getEventId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " event null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " không có quyền chủ event");
        }
        if (!organizationCommittee.getRole().getId().equals(ERole.ROLE_ORGANIZER)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Không có quyền chủ!");
        }
        List<String> userIdList = collaborationForm.getUserIdList();
        for (String IdUser : userIdList) {
            User userReceiver = userRepository.findById(IdUser)
                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " user null"));
            OrganizationCommittee collaborator = OrganizationCommittee.builder()
                    .status(EOrganizationCommitteeStatus.PENDING)
                    .event(event)
                    .role(roleRepository.getById(ERole.ROLE_COLLABORATOR))
                    .user(userReceiver)
                    .build();
            organizationCommitteeRepository.save(collaborator);
            String id = IDGenerator.generateID(notificationRepository, 10);
            Notification notification = Notification
                    .builder()
                    .id(id)
                    .user(userReceiver)
                    .type("Collaborator Invitation ")
                    .content(user.getLastName() + " " + user.getFirstName() + " has sent you an invitation to become a collaborator of " + event.getEventName())// feedback = content lý do dừng sự kiện
                    .targetUrl(event.getId())
                    .build();
            notificationRepository.saveAndFlush(notification);
            template.convertAndSend("/topic/notification/" + IdUser, notification);
//            config.setTo(userReceiver.getEmail());
//            config.setSubject("Event collaboration invitation");
//            config.setTemplate("invitation");
//            Map<String, Object> templateModel = new HashMap<>();
//            templateModel.put("fullName", userReceiver.getLastName() + " " + userReceiver.getFirstName());
//            templateModel.put("sender", user.getLastName() + " " + user.getFirstName());
//            templateModel.put("eventName", event.getEventName());
//            config.setTemplateModel(templateModel);
//            try {
//                emailService.sendEmail(Collections.singletonList(config));
//            } catch (MessagingException e) {
//                e.printStackTrace();
//            }
        }
        return true;
    }

    @Override
    public PagedResponse<ParticipantReq> getAllEventParticipant(final String userId, final String eventId, final String search, final String status, final Integer page, final Integer size) {
        long totalElements;
        List<EventParticipation> eventParticipations;
        eventParticipations = eventParticipationDAO.filterEventParticipant(eventId, search, status, size, (page - 1) * size);
        totalElements = eventParticipationDAO.countfilterEventParticipant(eventId, search, status);
        List<ParticipantReq> participantReqs = null;
        if (!CollectionUtils.isEmpty(eventParticipations)) {
            participantReqs = eventParticipations.stream()
                    .map(eventParticipation -> {
                        ParticipantReq participantReq = ParticipantReq.builder()
                                .eventParticipationId(eventParticipation.getId())
                                .email(eventParticipation.getUser().getEmail())
                                .fullName(eventParticipation.getUser().getLastName() + " " + eventParticipation.getUser().getFirstName())
                                .status(eventParticipation.getStatus().name())
                                .build();
                        return participantReq;
                    }).collect(Collectors.toList());
        }
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        return new PagedResponse<>(participantReqs, page, size, totalElements, totalPages);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean deleteCollaboration(final String userId, final Long userEventRoleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " user null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findById(userEventRoleId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " userEvent null"));
        OrganizationCommittee checkUserRole = organizationCommitteeRepository.findByEventAndUserAndStatus(organizationCommittee.getEvent(), user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(checkUserRole)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Không có quyền organizer");
        }
        if (!checkUserRole.getRole().getId().equals(ERole.ROLE_ORGANIZER)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " Không có quyền organizer");
        }
        if (organizationCommittee.equals(checkUserRole)) {
            throw new ServerErrorException(message.getErrorBadRequest() + " organizer không tự cancel.");
        }
        organizationCommitteeRepository.delete(organizationCommittee);
        return true;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean cancelCollaboration(final String userId, final Long userEventRoleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " user null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findById(userEventRoleId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " userEvent null"));
        if (organizationCommittee.getRole().getId().equals(ERole.ROLE_ORGANIZER)) {
            throw new ServerErrorException(message.getErrorBadRequest() + " organizer không tự cancel.");
        }
        OrganizationCommittee checkUserRole = organizationCommitteeRepository.findByEventAndUserAndStatus(organizationCommittee.getEvent(), user, EOrganizationCommitteeStatus.ACCEPTED);
        if (checkUserRole.equals(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }
        organizationCommitteeRepository.delete(organizationCommittee);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public boolean stopEvent(final String userId, final RejectForm stopForm) {
        Event event = eventRepository.findById(stopForm.getEventId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " Event null"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));

        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + "Không có quyền chủ event");
        }

        event.setStatus(EEventStatus.STOPPED);
        eventRepository.save(event);
        List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEventAndStatus(event, EEventParticipationStatus.APPROVED);
        if (!CollectionUtils.isEmpty(eventParticipations)) {
            for (EventParticipation eventParticipation : eventParticipations) {
                String id = IDGenerator.generateID(notificationRepository, 10);
                User participation = eventParticipation.getUser();
                Notification notification = Notification
                        .builder()
                        .id(id)
                        .user(participation)
                        .type("Stop Event")
                        .content("Event " + event.getEventName() + " has been stopped! Reason: " + stopForm.getFeedBack())// feedback = content lý do dừng sự kiện
                        .targetUrl(stopForm.getEventId())
                        .build();
                notificationRepository.saveAndFlush(notification);
                template.convertAndSend("/topic/notification/" + participation.getId(), notification);
            }
        }
        return true;
    }

    @Override
    public PagedResponse<Event> getAllManagedEvent(final String userId, final String search, final String status, final String type, final Integer page, final Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getErrorUnauthorized()));

        long totalElements;
        List<Event> events;
        events = eventDAO.filterAllManagedEvent(userId, search, status, type, size, (page - 1) * size);
        totalElements = eventDAO.countAllManagedEvent(userId, search, status, type);
        if (!CollectionUtils.isEmpty(events)) {
            for (Event event : events) {
                OrganizationCommittee organizationCommittee = event.getOrganizationCommittees().stream()
                        .filter(p -> p.getRole().getId().equals(ERole.ROLE_ORGANIZER)).findFirst().orElse(null);
                assert organizationCommittee != null;
                User userR = organizationCommittee.getUser();
                event.setRequestedBy(userR.getLastName() + " " + userR.getFirstName());
            }
        }
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        return new PagedResponse<>(events, page, size, totalElements, totalPages);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String updateFeedBack(final String eventId, final String feedbackLink, final String userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " Event null"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));

        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, user, EOrganizationCommitteeStatus.ACCEPTED);
        if (Objects.isNull(organizationCommittee)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + "Không có quyền chủ event");
        }
        event.setFeedbackLink(feedbackLink);
        eventRepository.save(event);
        return feedbackLink;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public String updateEvent(final String userId, final EventCreate eventUpdate) {
        if (!StringUtils.hasLength(eventUpdate.getEventId())) {
            throw new ServerErrorException(message.getWarnNoData() + " Event null");
        }
        Event event = eventRepository.findById(eventUpdate.getEventId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " Event null"));
        eventRoomScheduleRepository.deleteAllByEvent(event);
        eventEquipmentRepository.deleteAllByEvent(event);
        eventTimelineRepository.deleteAllByEvent(event);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));

        if (!organizationCommitteeRepository.existsByUserAndEventAndRoleAndStatus(user, event, roleRepository.getById(ERole.ROLE_ORGANIZER), EOrganizationCommitteeStatus.ACCEPTED)) {
            throw new ServerErrorException(message.getErrorUnauthorized() + " không phải chủ event không thể update");
        }

        Event eventUp = DataBuilder.to(eventUpdate, Event.class);
        eventUp.setId(eventUpdate.getEventId());
        eventUp.setType(EEventType.valueOf(eventUpdate.getType()));
        if (StringUtils.hasLength(event.getBanner())) {
            eventUp.setBanner(event.getBanner());
        }
        eventUp.setCreatedAt(TimeUtils.comNowDatetime());
        eventUp.setUpdatedAt(TimeUtils.comNowDatetime());
        eventUp.setLocation(eventUpdate.getLocation());
        eventUp.setNote(eventUpdate.getNote());
        eventUp.setFeedbackLink(eventUpdate.getFeedbackLink());
        eventUp.setStatus(EEventStatus.NEW);
        eventUp.setApprovalRequired(eventUpdate.isApprovalRequired());
        eventUp.setMinNumOfParticipant(eventUpdate.getMinNumOfParticipant());
        eventUp.setMaxNumOfParticipant(eventUpdate.getMaxNumOfParticipant());
        eventUp.setParticipantDeviation(eventUpdate.getParticipantDeviation());
        eventUp.setCheckinRequired(ECheckinRequired.valueOf(eventUpdate.getCheckinRequired()));
        if (StringUtils.hasLength(eventUpdate.getRegisterStartTime())) {
            eventUp.setRegistrationStartTime(eventUpdate.getRegisterStartTime());
        }
        if (StringUtils.hasLength(eventUpdate.getRegisterEndTime())) {
            eventUp.setRegisterEndTime(eventUpdate.getRegisterEndTime());
        }
        if (user.getRole().getId().equals(ERole.ROLE_STAFF)) {
            eventUp.setStatus(EEventStatus.PENDING);
            eventUp.setCreatedBy(userId);
        }

        if (StringUtils.hasLength(eventUpdate.getSpeakers())) {
            eventUp.setSpeakers(eventUpdate.getSpeakers());
        }
        if (StringUtils.hasLength(eventUpdate.getMasterOfCeremonies())) {
            eventUp.setMasterOfCeremonies(eventUpdate.getMasterOfCeremonies());
        }
        eventRepository.save(eventUp);


        if (!CollectionUtils.isEmpty(eventUpdate.getRoomIds())) {
            List<Room> rooms = roomRepository.findAllByIdIn(eventUpdate.getRoomIds());
            if (!CollectionUtils.isEmpty(rooms)) {
                for (Room room : rooms) {
                    //check room đã có người dùng chưa
                    Integer roomCheck = eventRoomScheduleRepository.checkRoomAvailable(room.getId(), eventUpdate.getStartTime(), eventUpdate.getEndTime());
                    if (roomCheck > 0) {
                        throw new ExistenceException(message.getDuplicateData() + " Room Id: " + room.getId() + " đã được đặt!");
                    }
                    EventRoomSchedule eventRoomSchedule = EventRoomSchedule.builder()
                            .event(eventUp)
                            .room(room)
                            .build();
                    eventRoomScheduleRepository.save(eventRoomSchedule);
                }
            }
        }
        if (!CollectionUtils.isEmpty(eventUpdate.getEventEquipments())) {
            List<EventEquipmentForm> eventEquipmentForms = eventUpdate.getEventEquipments();
            if (!CollectionUtils.isEmpty(eventEquipmentForms)) {
                eventEquipmentForms.forEach(eventEquipmentForm -> {
                    //check số lượng mượn phải < số lượng equipment còn lại
                    if (eventEquipmentForm.getQuantity() > equipmentService.numberOfEquipmentAvailable(eventEquipmentForm.getEquipmentId(), eventEquipmentForm.getBorrowTime(), eventEquipmentForm.getReturnTime())) {
                        throw new ServerErrorException("Insufficient number of equipment - equipmentId: " + eventEquipmentForm.getEquipmentId());
                    }

                    EventEquipment eventEquipment = EventEquipment.builder()
                            .quantity(eventEquipmentForm.getQuantity())
                            .event(eventUp)
                            .borrowTime(eventEquipmentForm.getBorrowTime())
                            .returnTime(eventEquipmentForm.getReturnTime())
                            .status(EventEquipmentStatus.PENDING)
                            .equipment(equipmentRepository.findById(eventEquipmentForm.getEquipmentId())
                                    .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())))
                            .build();
                    if (ERole.ROLE_STAFF.equals(user.getRole().getId()) || ERole.ROLE_MANAGER.equals(user.getRole().getId())) {
                        eventEquipment.setStatus(EventEquipmentStatus.APPROVED);
                    }
                    eventEquipmentRepository.saveAndFlush(eventEquipment);
                });
            }
        }
        if (!CollectionUtils.isEmpty(eventUpdate.getEventTimelines())) {
            List<EventTimelineCreate> eventTimelineCreates = eventUpdate.getEventTimelines();
            if (!CollectionUtils.isEmpty(eventTimelineCreates)) {
                eventTimelineCreates.forEach(eventTimelineCreate -> {
                    EventTimeline eventTimeline = DataBuilder.to(eventTimelineCreate, EventTimeline.class);
                    eventTimeline.setEvent(eventUp);
                    eventTimelineRepository.saveAndFlush(eventTimeline);
                });
            }
        }
        return eventUp.getId();
    }

    @Override
    public DashboardEvent getListEventDashboard(final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));

        Pageable listAsc = PageRequest.of(0, 4, Sort.by("startTime").ascending());
        Pageable listDsc = PageRequest.of(0, 4, Sort.by("startTime").descending());
        Pageable pageable = PageRequest.of(0, 100, Sort.by("startTime").descending());
        List<Event> events = eventRepository.getTopEventByStatus(EEventStatus.RUNNING, pageable).getContent();
        List<Event> yourRunningEvents = events.stream()
                .filter(event -> Objects.nonNull(eventParticipationRepository.findByEventAndUserAndStatusIn(event, user
                        , Arrays.asList(EEventParticipationStatus.APPROVED, EEventParticipationStatus.CHECKED_IN, EEventParticipationStatus.CHECKED_OUT)))
                ).collect(Collectors.toList());

        List<Event> runningEvents = events.stream()
                .filter(event -> Objects.isNull(eventParticipationRepository.findByEventAndUserAndStatusIn(event, user
                        , Arrays.asList(EEventParticipationStatus.APPROVED, EEventParticipationStatus.CHECKED_IN, EEventParticipationStatus.CHECKED_OUT)))
                ).collect(Collectors.toList());

        List<Event> approvedEvents = eventRepository.getTopEventByStatus(EEventStatus.APPROVED, listAsc).getContent();
        List<Event> finishedEvents = eventRepository.getTopEventByStatus(EEventStatus.FINISHED, listDsc).getContent();
        DashboardEvent dashboardEvent = DashboardEvent.builder()
                .yourRunningEvents(yourRunningEvents)
                .runningEvents(runningEvents)
                .approvedEvents(approvedEvents)
                .finishedEvents(finishedEvents)
                .build();
        return dashboardEvent;
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean acceptCollaboration(final String userId, final Long userEventRoleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findById(userEventRoleId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " userEventRole null"));
        if (organizationCommittee.getUser().equals(user) && organizationCommittee.getStatus().equals(EOrganizationCommitteeStatus.PENDING)) {
            organizationCommittee.setStatus(EOrganizationCommitteeStatus.ACCEPTED);
            organizationCommitteeRepository.save(organizationCommittee);
            EventParticipation eventParticipation = eventParticipationRepository.findEventParticipationByUserAndEvent(user, organizationCommittee.getEvent())
                    .orElse(null);
            if (Objects.nonNull(eventParticipation)) {
                eventParticipationRepository.delete(eventParticipation);
            }
            return true;
        }
        throw new ServerErrorException(message.getErrorUnauthorized());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean rejectCollaboration(final String userId, final Long userEventRoleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " User null"));
        OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findById(userEventRoleId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " userEventRole null"));
        if (organizationCommittee.getUser().equals(user) && organizationCommittee.getStatus().equals(EOrganizationCommitteeStatus.PENDING)) {
            organizationCommittee.setStatus(EOrganizationCommitteeStatus.REJECTED);
            organizationCommitteeRepository.save(organizationCommittee);
            return true;
        }
        throw new ServerErrorException(message.getErrorUnauthorized());
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean updateEventRoom(final String userId, final EventRoomForm eventRoomForm) {
        Event event = eventRepository.findById(eventRoomForm.getEventId())
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " Event null"));
        eventRoomScheduleRepository.deleteAllByEvent(event);

        if (!CollectionUtils.isEmpty(eventRoomForm.getRoomIds())) {
            List<Room> rooms = roomRepository.findAllByIdIn(eventRoomForm.getRoomIds());
            if (!CollectionUtils.isEmpty(rooms)) {
                for (Room room : rooms) {
                    //check room đã có người dùng chưa
                    Integer roomCheck = eventRoomScheduleRepository.checkRoomAvailable(room.getId(), event.getStartTime(), event.getEndTime());
                    if (roomCheck > 0) {
                        throw new ExistenceException(message.getDuplicateData() + " Room Id: " + room.getId() + " đã được đặt!");
                    }
                    EventRoomSchedule eventRoomSchedule = EventRoomSchedule.builder()
                            .event(event)
                            .room(room)
                            .build();
                    eventRoomScheduleRepository.save(eventRoomSchedule);
                }
            }
        }
        return true;
    }

    @Override
    public PagedResponse<Event> getOrganizedEvents(final String userId, final String search, final List<String> status, final String type, final Integer page, final Integer size) {
        List<EEventStatus> statuses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(status)) {
            statuses = status.stream().map(EEventStatus::valueOf).collect(Collectors.toList());
        }
        List<Event> events = eventDAO.filterEventsBy(userId, ERole.ROLE_ORGANIZER, EOrganizationCommitteeStatus.ACCEPTED,
                search, statuses, type, size, (page - 1) * size);
        long totalElements = eventDAO.countfilterEventsBy(userId, ERole.ROLE_ORGANIZER, EOrganizationCommitteeStatus.ACCEPTED,
                search, statuses, type);

        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        return new PagedResponse<>(events, page, size, totalElements, totalPages);
    }

    @Override
    public PagedResponse<Event> getCollaboratingEvents(final String userId, final String search, final List<String> status, final String type, final Integer page, final Integer size) {
        List<EEventStatus> statuses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(status)) {
            statuses = status.stream().map(EEventStatus::valueOf).collect(Collectors.toList());
        }
        List<Event> events = eventDAO.filterEventsBy(userId, ERole.ROLE_COLLABORATOR, EOrganizationCommitteeStatus.ACCEPTED,
                search, statuses, type, size, (page - 1) * size);
        long totalElements = eventDAO.countfilterEventsBy(userId, ERole.ROLE_COLLABORATOR, EOrganizationCommitteeStatus.ACCEPTED,
                search, statuses, type);

        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        return new PagedResponse<>(events, page, size, totalElements, totalPages);
    }

    @Override
    public PagedResponse<Event> getParticipatingEvents(final String userId, final String search, final List<String> status, final String type, final Integer page, final Integer size) {
        List<EEventStatus> statuses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(status)) {
            statuses = status.stream().map(EEventStatus::valueOf).collect(Collectors.toList());
        }
        List<EEventParticipationStatus> participationStatuses = Arrays.asList(EEventParticipationStatus.APPROVED, EEventParticipationStatus.CHECKED_IN, EEventParticipationStatus.CHECKED_OUT);
        List<Event> events = eventDAO.filterParticipatingEvents(userId, participationStatuses, search, statuses, type, size, (page - 1) * size);
        long totalElements = eventDAO.countParticipatingEvents(userId, participationStatuses, search, statuses, type);

        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));
        return new PagedResponse<>(events, page, size, totalElements, totalPages);
    }
}
