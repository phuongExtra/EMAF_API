package com.emaf.service.service;

import com.emaf.service.entity.EventConfiguration;
import com.emaf.service.model.EventConfig.EventConfigCreateForm;
import com.emaf.service.model.common.PagedResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * EventConfigurationService
 *
 * @author: PhuongLN
 * @since: 2023/01/04
 */
@Service
public interface EventConfigurationService {

    EventConfiguration getEventConfig();

    boolean createEventConfig(EventConfigCreateForm eventConfigCreateForm, String userId);

    PagedResponse<EventConfiguration> getAllEventConfigs(Integer page, Integer size, String status);

    boolean updateEventConfig(EventConfigCreateForm eventConfigUpdateForm, String userId);

    EventConfiguration getEventConf();
}