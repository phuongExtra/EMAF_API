package com.emaf.service.controller;

import com.emaf.service.entity.EventParticipation;
import com.emaf.service.entity.OrganizationCommittee;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.EventParticipationService;
import com.emaf.service.service.OrganizationCommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * EventParticipationController
 *
 * @author: PhuongLN
 * @since: 2022/12/12
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/participation")
public class EventParticipationController {

    @Autowired
    private EventParticipationService eventParticipationService;

    @Autowired
    private AccessTokenService accessTokenService;

    @GetMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EventParticipation> getParticipationRequests(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return eventParticipationService.getParticipationRequests(userId);
    }
}