package com.emaf.service.repository;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.Role;
import com.emaf.service.entity.User;
import com.emaf.service.entity.OrganizationCommittee;
import com.emaf.service.enumeration.EOrganizationCommitteeStatus;
import com.emaf.service.enumeration.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * OrganizationCommitteeRepository
 *
 * @author: VuongVT2
 * @since: 2022/10/30
 */
@Repository
public interface OrganizationCommitteeRepository extends JpaRepository<OrganizationCommittee, Long> {

    Optional<OrganizationCommittee> findByEventAndRole(Event event, Role role);

    OrganizationCommittee findByEventAndUserAndStatus(Event event, User user, EOrganizationCommitteeStatus status);

    boolean existsByUserAndEventAndStatus(User user, Event event, EOrganizationCommitteeStatus status);

    boolean existsByUserAndEventAndRoleAndStatus(User user, Event event, Role role, EOrganizationCommitteeStatus status);

    void deleteAllByEvent(Event event);

    @Query(value = "SELECT eoc " +
            "FROM OrganizationCommittee eoc LEFT JOIN Event ee ON eoc.event.id = ee.id " +
            "WHERE eoc.user = :user " +
            "AND eoc.status = :status " +
            "AND eoc.role = :role")
    List<OrganizationCommittee> findByUserAndStatusAndRole(@Param(value = "user") User user,
                                                           @Param(value = "status") EOrganizationCommitteeStatus status,
                                                           @Param(value = "role") Role role);
}
