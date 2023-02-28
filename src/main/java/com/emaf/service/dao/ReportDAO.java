package com.emaf.service.dao;

import com.emaf.service.enumeration.EEventStatus;

import java.util.List;

/**
 * ReportDAO
 *
 * @author: VuongVT2
 * @since: 2023/01/09
 */
public interface ReportDAO {
    Long numberOfEventBy(String fromDate, String toDate, List<EEventStatus> statuses);
}
