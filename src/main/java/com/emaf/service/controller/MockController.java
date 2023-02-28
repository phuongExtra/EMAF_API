package com.emaf.service.controller;

import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.utils.IDGenerator;
import com.emaf.service.common.utils.TimeUtils;
import com.emaf.service.entity.*;
import com.emaf.service.enumeration.*;
import com.emaf.service.repository.*;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * MockController
 *
 * @author: VuongVT2
 * @since: 2022/10/27
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/mock")
public class MockController {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventRoomScheduleRepository eventRoomScheduleRepository;

    @Autowired
    private EventEquipmentRepository eventEquipmentRepository;

    @Autowired
    private EventMajorRepository eventMajorRepository;

    @Autowired
    private EventTimelineRepository eventTimelineRepository;


    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initStatic(@RequestParam(value = "student", defaultValue = "0") @Positive int numberOfStudent,
                                        @RequestParam(value = "manager", defaultValue = "0") @Positive int numberOfManager,
                                        @RequestParam(value = "staff", defaultValue = "0") @Positive int numberOfStaff,
                                        @RequestParam(value = "equipment", defaultValue = "5") @Positive int numberOfEquipment,
                                        @RequestParam(value = "event", defaultValue = "0") @Positive int numberOfEvents
    ) throws IOException, URISyntaxException {
        roleRepository.deleteAll();
        majorRepository.deleteAll();
        equipmentRepository.deleteAll();
        roomRepository.deleteAll();

        // Mock static data
        MockJSONUtils.initStatic("roles.json", Role[].class, roleRepository);
        MockJSONUtils.initStatic("majors.json", Major[].class, majorRepository);
        MockJSONUtils.initStatic("rooms.json", Room[].class, roomRepository);

        Faker faker = new Faker();
        List<Major> majors = majorRepository.findAll();
        Major major = majors.get(faker.random().nextInt(majors.size()));

        String id1 = IDGenerator.generateID(userRepository, 10);
        User user1 = User.builder()
                .id(id1)
                .email("khalse63295@gmail.com")
                .firstName("Lê")
                .lastName("Kha")
                .phoneNumber(faker.phoneNumber().cellPhone().substring(12))
                .status(EUserStatus.ACTIVE)
                .role(roleRepository.getById(ERole.ROLE_STUDENT))
                .major(major)
                .build();
        userRepository.saveAndFlush(user1);

        String id2 = IDGenerator.generateID(userRepository, 10);
        String id3 = IDGenerator.generateID(userRepository, 10);
        User user2 = User.builder()
                .id(id2)
                .email("leduonghoangphuc3004@gmail.com")
                .firstName("Phúc")
                .lastName("Hoàng")
                .phoneNumber(faker.phoneNumber().cellPhone().substring(12))
                .status(EUserStatus.ACTIVE)
                .role(roleRepository.getById(ERole.ROLE_MANAGER))
                .major(null)
                .build();
        userRepository.saveAndFlush(user2);

        User user3 = User.builder()
                .id(id3)
                .email("phucldhse130602@fpt.edu.vn")
                .firstName("Phúc")
                .lastName("Hoàng")
                .phoneNumber(faker.phoneNumber().cellPhone().substring(12))
                .status(EUserStatus.ACTIVE)
                .role(roleRepository.getById(ERole.ROLE_STAFF))
                .major(null)
                .build();
        userRepository.saveAndFlush(user3);
        // Mock user
        mockUser(numberOfStudent, ERole.ROLE_STUDENT);
        mockUser(1, ERole.ROLE_ADMIN);
        mockUser(numberOfStaff, ERole.ROLE_STAFF);
        mockUser(numberOfManager, ERole.ROLE_MANAGER);
        mockEquipment(numberOfEquipment);
        mockEvents(numberOfEvents, EEventStatus.APPROVED);
        mockEvents(10, EEventStatus.NEW);
        mockEvents(15, EEventStatus.PENDING);
        mockEvents(10, EEventStatus.REJECTED);
        mockEvents(5, EEventStatus.CANCELLED);


        return ResponseEntity.ok(userRepository.findAll()
                .stream()
                .map(user -> {
                    Map<String, String> results = new HashMap<>();
                    results.put("id", user.getId());
                    results.put("email", user.getEmail());
                    results.put("role", user.getRole().getId().name());

                    return results;
                })
                .collect(Collectors.toList()));
    }


    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init-event-new", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> mockEvents(final int numberOfEvents, EEventStatus status) {
        eventRoomScheduleRepository.deleteAll();
        eventTimelineRepository.deleteAll();
        eventMajorRepository.deleteAll();
        eventEquipmentRepository.deleteAll();
        eventEquipmentRepository.deleteAll();
        eventRepository.deleteAll();
        Faker faker = new Faker();
        List<EEventType> eEventTypes = Arrays.asList(EEventType.values());
        LocalDateTime startTime = LocalDateTime.now();
        List<User> students = userRepository.findByRole(roleRepository.getById(ERole.ROLE_STUDENT));
        List<User> managers = userRepository.findByRole(roleRepository.getById(ERole.ROLE_MANAGER));
        List<Room> rooms = roomRepository.findAll();
        List<Major> majors = majorRepository.findAll();
        List<Equipment> equipmentList = equipmentRepository.findAll();

        for (int i = 0; i < numberOfEvents; i++) {
            String id = IDGenerator.generateID(equipmentRepository, 10);
            startTime = startTime.plusDays(1);
            String endTime = startTime.plusHours(faker.random().nextInt(1, 6)).format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            Event event = Event.builder()
                    .id(id)
                    .banner("https://img.freepik.com/free-psd/horizontal-banner-template-professional-business-event_23-2149313272.jpg?w=2000")
                    .eventName(faker.company().name())
                    .shortDescription(faker.lorem().sentence(5))
                    .description(faker.lorem().sentence(20))
                    .type(eEventTypes.get(faker.random().nextInt(eEventTypes.size())))
                    .status(status)
                    .startTime(startTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")))
                    .endTime(endTime)
                    .createdAt(TimeUtils.comNowDatetime())
                    .updatedAt(TimeUtils.comNowDatetime())
                    .createdBy(students.get(faker.random().nextInt(students.size())).getId())
                    .handleBy(managers.get(faker.random().nextInt(managers.size())).getId())
                    .build();

            eventRepository.save(event);
            EventRoomSchedule eventRoomSchedule = EventRoomSchedule.builder()
                    .room(rooms.get(faker.random().nextInt(rooms.size())))
                    .event(event)
                    .build();
            eventRoomScheduleRepository.save(eventRoomSchedule);

            Equipment equipment = equipmentList.get(faker.random().nextInt(0, equipmentList.size() / 2));
            EventEquipment eventEquipment = EventEquipment.builder()
                    .event(event)
                    .equipment(equipment)
                    .quantity(faker.random().nextInt(1, equipment.getQuantity()))
                    .build();
            eventEquipmentRepository.save(eventEquipment);
            Equipment equipment2 = equipmentList.get(faker.random().nextInt(equipmentList.size() / 2, equipmentList.size() - 1));
            EventEquipment eventEquipment2 = EventEquipment.builder()
                    .event(event)
                    .equipment(equipment2)
                    .quantity(faker.random().nextInt(1, equipment2.getQuantity()))
                    .build();
            eventEquipmentRepository.save(eventEquipment2);

            EventMajor eventMajor = EventMajor.builder()
                    .event(event)
                    .major(majors.get(faker.random().nextInt(majors.size())))
                    .build();
            eventMajorRepository.save(eventMajor);

            long i1 = Long.parseLong(event.getStartTime()) + 30;
            EventTimeline eventTimeline1 = EventTimeline.builder()
                    .event(event)
                    .startTime(event.getStartTime())
                    .endTime("" + i1)
                    .note(faker.lorem().sentence(1))
                    .content(faker.lorem().sentence(2))
                    .activity(faker.lorem().sentence(1))
                    .build();
            eventTimelineRepository.save(eventTimeline1);

            EventTimeline eventTimeline2 = EventTimeline.builder()
                    .event(event)
                    .startTime(eventTimeline1.getEndTime())
                    .endTime(event.getEndTime())
                    .note(faker.lorem().sentence(1))
                    .content(faker.lorem().sentence(2))
                    .activity(faker.lorem().sentence(1))
                    .build();
            eventTimelineRepository.save(eventTimeline2);

        }
        return ResponseEntity.ok(eventRepository.findAll()
                .stream()
                .map(event -> {
                    Map<String, String> results = new HashMap<>();
                    results.put("id", event.getId());
                    results.put("eventName", event.getEventName());
                    return results;
                })
                .collect(Collectors.toList()));
    }

    private void mockEquipment(int numberOfEquipment) {
        User user = userRepository.findByRole(roleRepository.getById(ERole.ROLE_STAFF)).get(0);
        Faker faker = new Faker();
        for (int i = 0; i < numberOfEquipment; i++) {
            String id = IDGenerator.generateID(equipmentRepository, 10);
            Equipment equipment = Equipment.builder()
                    .id(id)
                    .equipmentName(String.join(" ", faker.lorem().words(5)))
                    .image("")
                    .note("")
                    .quantity(faker.random().nextInt(1, 20))
                    .createdBy(user.getId())
                    .updatedBy(user.getId())
                    .status(EEquipmentStatus.AVAILABLE)
                    .build();
            equipmentRepository.save(equipment);

        }
    }

    private void mockUser(int numberOf, ERole role) {
        Faker faker = new Faker();
        List<Major> majors = majorRepository.findAll();
        Major major = majors.get(faker.random().nextInt(majors.size()));


        for (int i = 0; i < numberOf; i++) {
            String id = IDGenerator.generateID(userRepository, 10);
            User user = User.builder()
                    .id(id)
                    .email(faker.internet().emailAddress())
                    .firstName(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .phoneNumber(faker.phoneNumber().cellPhone().substring(12))
                    .status(EUserStatus.ACTIVE)
                    .role(roleRepository.findById(role).orElseThrow(() -> new ServerErrorException("Mock null Role")))
                    .major(ERole.ROLE_STUDENT.equals(role) ? major : null)
                    .build();
            userRepository.saveAndFlush(user);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init-event", produces = MediaType.APPLICATION_JSON_VALUE)
    public void mockEvent() throws FileNotFoundException {
        Faker faker = new Faker();
        List<Event> events = MockJSONUtils.getFromJson("events.json", Event[].class);
        List<Room> room = roomRepository.findAll();
        for (Event event : events) {
            String id = IDGenerator.generateID(userRepository, 10);
            event.setId(id);
            event.setHandleBy("IUBOK9HFAQ");//Phuc
            eventRepository.save(event);
//            EventTimeline
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    @GetMapping(value = "/init-equpiment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> initEquipment() throws FileNotFoundException {
        Faker faker = new Faker();
        List<Equipment> equipmentList = MockJSONUtils.getFromJson("equipments.json", Equipment[].class);
        for (Equipment equipment : equipmentList) {
            String id = IDGenerator.generateID(userRepository, 10);
            equipment.setId(id);
            equipment.setStatus(EEquipmentStatus.AVAILABLE);
            equipmentRepository.save(equipment);
        }
        return ResponseEntity.ok(equipmentRepository.findAll()
                .stream()
                .map(equipment -> {
                    Map<String, String> results = new HashMap<>();
                    results.put("id", equipment.getId());
                    results.put("equipmentName", equipment.getEquipmentName());
                    return results;
                })
                .collect(Collectors.toList()));
    }

}

class MockJSONUtils<S> {
    public static <S> void initStatic(String fileName, Class<S[]> clazz, JpaRepository jpa) throws FileNotFoundException {
        saveFromJson(fileName, clazz, jpa);
    }

    public static void delete(JpaRepository jpa) {
        jpa.deleteAll();
    }

    public static <S> void saveAll(JpaRepository jpa, List<S> entities) {
        jpa.saveAll(entities);
        jpa.flush();
    }

    public static <S> void saveFromJson(String fileName, Class<S[]> clazz, JpaRepository jpa) throws FileNotFoundException {
        jpa.saveAll(getFromJson(fileName, clazz));
        jpa.flush();
    }

    public static <S> List<S> getFromJson(String fileName, Class<S[]> clazz) throws FileNotFoundException {
        Gson gson = new Gson();
        InputStream inputStream = MockJSONUtils.class.getClassLoader().getResourceAsStream("static/json/" + fileName);

        S[] entities = gson.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), clazz);

        return Arrays.asList(entities);
    }
}