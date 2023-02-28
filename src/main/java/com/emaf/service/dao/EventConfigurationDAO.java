package com.emaf.service.dao;

import com.emaf.service.entity.EventConfiguration;

import java.util.List;

/**
 * EventConfigurationDAO
 *
 * @author: PhuongLN
 * @since: 2023/01/04
 */
public interface EventConfigurationDAO {
    List<EventConfiguration> filterEventConfigs(long limit, long offset, String status);

    long countEventConfigs(String status);
}