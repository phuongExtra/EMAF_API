package com.emaf.service.enumeration;

/**
 * EEventStatus
 *
 * @author: VuongVT2
 * @since: 2022/09/14
 */
public enum EEventStatus {
    NEW,//student create
    PENDING,// staff approve student event hoặc create event
    APPROVED, //manager approve pending event
    REJECTED,//manager reject pending event
    CANCELLED,////manager cancel pending event
    RUNNING,
    FINISHED,
    STOPPED
}
