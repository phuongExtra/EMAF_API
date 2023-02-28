package com.emaf.service.controller;

import com.emaf.service.common.constant.AppConstant;
import com.emaf.service.entity.Event;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.model.event.EventReportData;
import com.emaf.service.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * ReportController
 *
 * @author: VuongVT2
 * @since: 2022/12/16
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping(value = "export", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exportAttendance(@RequestParam(value = "eventId") String eventId,
                                   HttpServletRequest request, HttpServletResponse response) {
        return reportService.exportAttendance(eventId, request, response);
    }

    @Operation(description = "Đếm số event trong khoảng ngày (format giống startTime trong event) và theo status Event")
    @GetMapping(value = "count-events", produces = MediaType.TEXT_PLAIN_VALUE)
    public Long numberOfEventBy(@RequestParam(value = "fromDate") String fromDate,
                                @RequestParam(value = "toDate") String toDate,
                                @RequestParam(value = "status") List<String> statuses) {
        return reportService.numberOfEventBy(fromDate, toDate, statuses);
    }

    @GetMapping(value = "export-list", produces = MediaType.TEXT_PLAIN_VALUE)
    public String exportListEvent(@RequestParam(value = "startTime") String startTime,
                                  @RequestParam(value = "endTime") String endTime,
                                  HttpServletRequest request, HttpServletResponse response) {
        return reportService.exportListEvent(startTime, endTime, request, response);
    }
}
