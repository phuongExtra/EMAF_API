package com.emaf.service.controller;

import com.emaf.service.common.constant.AppConstant;
import com.emaf.service.entity.EventConfiguration;
import com.emaf.service.model.EventConfig.EventConfigCreateForm;
import com.emaf.service.model.common.PagedResponse;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.EventConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * EventConfigurationController
 *
 * @author: PhuongLN
 * @since: 2023/01/04
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/event-configuration")
public class EventConfigurationController {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private EventConfigurationService eventConfigurationService;

    @GetMapping(value = "get-configs", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventConfiguration getEventConfig() {
        return eventConfigurationService.getEventConfig();
    }

    @GetMapping(value = "get-config", produces = MediaType.APPLICATION_JSON_VALUE)
    public EventConfiguration getEventConf() {
        return eventConfigurationService.getEventConf();
    }

    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public PagedResponse<EventConfiguration> getAllEventConfigs(@RequestParam(name = "page", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer page,
                                                                @RequestParam(name = "size", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer size,
                                                                @RequestParam(name = "status") String status){
        return eventConfigurationService.getAllEventConfigs(page, size, status);
    }

    @PostMapping(value = "create-config", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean createEventConfig(@RequestBody @Valid EventConfigCreateForm eventConfigCreateForm ,
                                     HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventConfigurationService.createEventConfig(eventConfigCreateForm, userId);
    }

    @PostMapping(value = "update-config", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateEventConfig(@RequestBody @Valid EventConfigCreateForm eventConfigUpdateForm ,
                                     HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventConfigurationService.updateEventConfig(eventConfigUpdateForm, userId);
    }

}