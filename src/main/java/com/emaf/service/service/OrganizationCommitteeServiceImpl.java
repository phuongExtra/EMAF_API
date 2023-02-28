package com.emaf.service.service;

import com.emaf.service.common.constant.Message;
import com.emaf.service.common.exception.ServerErrorException;
import com.emaf.service.entity.OrganizationCommittee;
import com.emaf.service.entity.User;
import com.emaf.service.enumeration.EOrganizationCommitteeStatus;
import com.emaf.service.enumeration.ERole;
import com.emaf.service.repository.OrganizationCommitteeRepository;
import com.emaf.service.repository.RoleRepository;
import com.emaf.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OrganizationCommitteeServiceImpl
 *
 * @author: PhuongLN
 * @since: 2022/12/15
 */
@Service
public class OrganizationCommitteeServiceImpl implements OrganizationCommitteeService {

    @Autowired
    private UserRepository userRepository;

    private RoleRepository roleRepository;

    @Autowired
    private Message message;

    @Autowired
    private OrganizationCommitteeRepository organizationCommitteeRepository;

    public OrganizationCommitteeServiceImpl(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<OrganizationCommittee> getCollaborationInvitations(final String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ServerErrorException(message.getWarnUserNotFound()));

        List<OrganizationCommittee> organizationCommitteePendingList = organizationCommitteeRepository.findByUserAndStatusAndRole(user, EOrganizationCommitteeStatus.PENDING, roleRepository.findById(ERole.ROLE_COLLABORATOR)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())));
        List<OrganizationCommittee> organizationCommitteeRejectedList = organizationCommitteeRepository.findByUserAndStatusAndRole(user, EOrganizationCommitteeStatus.REJECTED, roleRepository.findById(ERole.ROLE_COLLABORATOR)
                .orElseThrow(() -> new ServerErrorException(message.getWarnNoData())));
        List<OrganizationCommittee> organizationCommitteeList = Stream.concat(organizationCommitteePendingList.stream(), organizationCommitteeRejectedList.stream()).collect(Collectors.toList());
        organizationCommitteeList = organizationCommitteeList
                .stream()
                .map(organizationCommittee -> {
                    organizationCommittee.setEventName(organizationCommittee.getEvent().getEventName());
                    organizationCommittee.setEventId(organizationCommittee.getEvent().getId());
                    organizationCommittee.setOrganizer(organizationCommittee.getUser().getLastName() + " " + organizationCommittee.getUser().getFirstName());
                    return organizationCommittee;
                })
                .collect(Collectors.toList());

        return organizationCommitteeList;
    }
}