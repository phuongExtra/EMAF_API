package com.emaf.service.repository;

import com.emaf.service.entity.EventConfiguration;
import com.emaf.service.enumeration.EEventConfigurationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EventConfigurationRepository
 *
 * @author: PhuongLN
 * @since: 2023/01/04
 */
@Repository
public interface EventConfigurationRepository extends JpaRepository<EventConfiguration, Long> {
    EventConfiguration findByStatus(EEventConfigurationStatus status);

}