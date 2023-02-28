package com.emaf.service.schedules;

import com.emaf.service.common.utils.TimeUtils;
import com.emaf.service.entity.Event;
import com.emaf.service.enumeration.EEventStatus;
import com.emaf.service.repository.EventRepository;
import com.emaf.service.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * EventTask
 *
 * @author: VuongVT2
 * @since: 2022/12/17
 */
@Component
public class EventTask {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(EventTask.class);

    @Scheduled(cron = "0 0/1 7-21 * * *") // 1 phút chạy 1 lan từ 7h->21h
    @Transactional(rollbackFor = Throwable.class)
    public void scheduleTaskEvent() {
        logger.info("Task start event: {}", new Date());
        String timeNow = TimeUtils.comNowYYYYMMddHHmm();
        List<Event> eventsStart = eventRepository.findAllByStartTimeAndStatus(timeNow, EEventStatus.APPROVED);
        logger.info("start event: {}", eventsStart);
        eventService.scheduleStartEvent(eventsStart);

        List<Event> eventsEnd = eventRepository.findAllByEndTimeAndStatus(timeNow, EEventStatus.RUNNING);
        logger.info("end event: {}", eventsEnd);
        eventService.scheduleEndEvent(eventsEnd);
    }
}
