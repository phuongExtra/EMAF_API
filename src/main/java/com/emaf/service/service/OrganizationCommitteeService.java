package com.emaf.service.service;

import com.emaf.service.entity.OrganizationCommittee;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * OrganizationCommitteeService
 *
 * @author: PhuongLN
 * @since: 2022/12/15
 */
@Service
public interface OrganizationCommitteeService {

    List<OrganizationCommittee> getCollaborationInvitations(String userId);
}