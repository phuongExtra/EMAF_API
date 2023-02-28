package com.emaf.service.controller;

import com.emaf.service.entity.OrganizationCommittee;
import com.emaf.service.security.service.AccessTokenService;
import com.emaf.service.service.DocumentService;
import com.emaf.service.service.OrganizationCommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * OrganizationCommitteeController
 *
 * @author: PhuongLN
 * @since: 2022/12/12
 */
@RestController
@RequestMapping(value = "/emaf/api/v1/collaboration")
public class OrganizationCommitteeController {

    @Autowired
    private OrganizationCommitteeService organizationCommitteeService;

    @Autowired
    private AccessTokenService accessTokenService;

    @GetMapping(value = "/invitations", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrganizationCommittee> getCollaborationInvitations(HttpServletRequest request) {
        String userId = accessTokenService.getUserID(request);
        return organizationCommitteeService.getCollaborationInvitations(userId);
    }
}