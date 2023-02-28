package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.logging.AppLogger;
import com.emaf.service.common.utils.ExcelExportList;
import com.emaf.service.common.utils.ExcelExporter;
import com.emaf.service.common.utils.TimeUtils;
import com.emaf.service.component.S3Component;
import com.emaf.service.dao.ReportDAO;
import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventParticipation;
import com.emaf.service.entity.OrganizationCommittee;
import com.emaf.service.enumeration.EEventParticipationStatus;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.enumeration.EOrganizationCommitteeStatus;
import com.emaf.service.enumeration.ERole;
import com.emaf.service.model.ParticipationData;
import com.emaf.service.model.event.EventListData;
import com.emaf.service.repository.EventParticipationRepository;
import com.emaf.service.repository.EventRepository;
import com.emaf.service.repository.OrganizationCommitteeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ReportServiceImpl
 *
 * @author: VuongVT2
 * @since: 2022/12/17
 */
@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventParticipationRepository eventParticipationRepository;

    @Autowired
    private OrganizationCommitteeRepository organizationCommitteeRepository;

    @Autowired
    private EventService eventService;

    @Autowired
    private S3Component s3Component;

    @Autowired
    private Message message;

    @Autowired
    private ReportDAO reportDAO;


    @Override
    public String exportAttendance(final String eventId, final HttpServletRequest request, HttpServletResponse response) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData() + " Event null"));
        List<EventParticipation> eventParticipations = eventParticipationRepository.findAllByEventAndStatusNotIn(event, Arrays.asList(EEventParticipationStatus.NEW, EEventParticipationStatus.REJECTED));

        List<ParticipationData> participationDataList = eventParticipations.stream()
                .map(eventParticipation -> {
                    ParticipationData participationData = ParticipationData.builder()
                            .studentCode(eventParticipation.getUser().getStudentCode())
                            .fullName(eventParticipation.getUser().getLastName() + " " + eventParticipation.getUser().getFirstName())
                            .email(eventParticipation.getUser().getEmail())
                            .eventName(eventParticipation.getEvent().getEventName())
                            .status(eventParticipation.getStatus().name())
                            .checkinTime(StringUtils.hasLength(eventParticipation.getCheckinTime()) ? TimeUtils.convertStrToDate(eventParticipation.getCheckinTime()) : null)
                            .checkoutTime(StringUtils.hasLength(eventParticipation.getCheckoutTime()) ? TimeUtils.convertStrToDate(eventParticipation.getCheckoutTime()) : null)
                            .build();

                    OrganizationCommittee organizationCommittee = organizationCommitteeRepository.findByEventAndUserAndStatus(event, eventParticipation.getUser(), EOrganizationCommitteeStatus.ACCEPTED);
                    if (Objects.isNull(organizationCommittee)) {
                        participationData.setRole("Participation");
                    } else {
                        if (organizationCommittee.getRole().getId().equals(ERole.ROLE_ORGANIZER)) {
                            participationData.setRole("Organizer");
                        } else {
                            participationData.setRole("Collaborator");
                        }
                    }
                    return participationData;
                }).collect(Collectors.toList());

        String fileName = "Event_Report.xlsx";
        String directoryName = "report/" + eventId;
        String key = directoryName + "/" + fileName;

        File directory = new File(System.getProperty("user.dir") + "/" + directoryName);
        if (!directory.exists()) directory.mkdirs();

        ExcelExporter excelExporter = new ExcelExporter(key, participationDataList);
        excelExporter.export();

        File file = new File(System.getProperty("user.dir") + "/" + key);
        String reportUrl = s3Component.get(key);
        if (!StringUtils.hasLength(reportUrl)) {
            try {
                reportUrl = s3Component.upload("report/" + eventId, file);
            } catch (URISyntaxException e) {
                AppLogger.errorLog(e.getMessage(), e);
                throw new ServerErrorException(message.getErrorUploadFileError());
            }
        }
        return reportUrl;
    }

    @Override
    public Long numberOfEventBy(final String fromDate, final String toDate, final List<String> statuses) {
        List<EEventStatus> eventStatuses = new ArrayList<>();
        if (!CollectionUtils.isEmpty(statuses)) {
            eventStatuses = statuses.stream().map(EEventStatus::valueOf).collect(Collectors.toList());
        }
        return reportDAO.numberOfEventBy(fromDate, toDate, eventStatuses);
    }


    @Override
    public String exportListEvent(final String startTime, final String endTime, final HttpServletRequest request, final HttpServletResponse response) {
        String fileName = "Event_List_Report.xlsx";
        String time = TimeUtils.comNowDatetime();
        String directoryName = "report/" + time;
        String key = directoryName + "/" + fileName;

        File directory = new File(System.getProperty("user.dir") + "/" + directoryName);
        if (!directory.exists()) directory.mkdirs();

        List<EventListData> eventListData = eventService.getChartData(startTime, endTime);

        ExcelExportList excelExporter = new ExcelExportList(key, eventListData, startTime, endTime, eventRepository);
        excelExporter.export();

        File file = new File(System.getProperty("user.dir") + "/" + key);
        String reportUrl = s3Component.get(key);
        if (!StringUtils.hasLength(reportUrl)) {
            try {
                reportUrl = s3Component.upload("report/" + time, file);
            } catch (URISyntaxException e) {
                AppLogger.errorLog(e.getMessage(), e);
                throw new ServerErrorException(message.getErrorUploadFileError());
            }
        }
        return reportUrl;
    }


}

