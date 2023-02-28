package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.common.utils.DataBuilder;
import com.emaf.service.common.utils.TimeUtils;
import com.emaf.service.dao.EventConfigurationDAO;
import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventConfiguration;
import com.emaf.service.entity.User;
import com.emaf.service.enumeration.EEventConfigurationStatus;
import com.emaf.service.enumeration.ERole;
import com.emaf.service.model.EventConfig.EventConfigCreateForm;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.repository.EventConfigurationRepository;
import com.emaf.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * EventConfigurationServiceImpl
 *
 * @author: PhuongLN
 * @since: 2023/01/04
 */
@Service
public class EventConfigurationServiceImpl implements EventConfigurationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventConfigurationRepository eventConfigurationRepository;

    @Autowired
    private Message message;

    @Autowired
    private EventConfigurationDAO eventConfigurationDAO;

    @Override
    public EventConfiguration getEventConfig() {
        EventConfiguration eventConfiguration = eventConfigurationRepository.findByStatus(EEventConfigurationStatus.ACTIVE);
        
        return eventConfiguration;
    }


    @Override
    public boolean createEventConfig(final EventConfigCreateForm eventConfigCreateForm, final String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));

        if (!ERole.ROLE_STAFF.name().equals(user.getRole().getId().name())) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        EventConfiguration eventConfiguration = DataBuilder.to(eventConfigCreateForm, EventConfiguration.class);
        eventConfiguration.setCreatedAt(TimeUtils.comNowDatetime());
        eventConfiguration.setUpdatedAt(TimeUtils.comNowDatetime());

        eventConfigurationRepository.saveAndFlush(eventConfiguration);
        return true;
    }

    @Override
    public PagedResponse<EventConfiguration> getAllEventConfigs(final Integer page, final Integer size, String status) {
        List<EventConfiguration> eventConfigurationList = eventConfigurationDAO.filterEventConfigs(size, (page - 1) * size, status);
        long totalElements = eventConfigurationDAO.countEventConfigs(status);
        long totalPages = (long) Math.ceil(totalElements / (size * 1.0));

        return new PagedResponse<>(eventConfigurationList, page, size, totalElements, totalPages);
    }

    @Override
    public boolean updateEventConfig(final EventConfigCreateForm eventConfigUpdateForm, final String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));

        if (!ERole.ROLE_STAFF.name().equals(user.getRole().getId().name())) {
            throw new ServerErrorException(message.getErrorUnauthorized());
        }

        EventConfiguration eventConfiguration = eventConfigurationRepository.findAll().get(0);
        eventConfiguration.setUpdatedAt(TimeUtils.comNowDatetime());
        if (eventConfigUpdateForm.getStatus().equals(EEventConfigurationStatus.INACTIVE.name())) {
            eventConfiguration.setStatus(EEventConfigurationStatus.INACTIVE);
        } else {
            eventConfiguration.setStatus(EEventConfigurationStatus.ACTIVE);
        }
        eventConfiguration.setCheckinOpenTime(eventConfigUpdateForm.getCheckinOpenTime());
        eventConfiguration.setCheckoutOpenTime(eventConfigUpdateForm.getCheckoutOpenTime());
        eventConfiguration.setRequestLimitPeriod(eventConfigUpdateForm.getRequestLimitPeriod());

        eventConfigurationRepository.saveAndFlush(eventConfiguration);
        return true;
    }

    @Override
    public EventConfiguration getEventConf() {
        return eventConfigurationRepository.findAll().stream().findFirst().orElse(null);
    }

}