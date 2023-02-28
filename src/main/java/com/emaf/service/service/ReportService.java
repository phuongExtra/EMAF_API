package com.emaf.service.service;

import com.emaf.service.model.event.EventReportData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * ReportService
 *
 * @author: VuongVT2
 * @since: 2022/12/17
 */
public interface ReportService {
    String exportAttendance(String eventId, HttpServletRequest request,HttpServletResponse response);

    Long numberOfEventBy(String fromDate, String toDate, List<String> statuses);

    String exportListEvent(String startTime, String endTime, HttpServletRequest request, HttpServletResponse response);
}
