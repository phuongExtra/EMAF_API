package com.emaf.service.repository;

import com.emaf.service.entity.OrganizationCommittee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventUserRepository extends JpaRepository<OrganizationCommittee, Long>, JpaSpecificationExecutor<OrganizationCommittee> {

}