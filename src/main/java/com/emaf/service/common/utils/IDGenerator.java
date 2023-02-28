package com.emaf.service.common.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * IDGenerator
 *
 * @author khal
 * @since 2022/01/11
 */
public class IDGenerator {

    /**
     * Generate ID by JPA Repository and length of entity's id
     *
     * @param jpaRepository Entity JPARepository
     * @param length ID length
     * @return String - ID
     */
    public static String generateID(final JpaRepository jpaRepository, final int length) {
        String id;
        do {
            id = RandomStringUtils.randomAlphanumeric(length).toUpperCase();
        } while (jpaRepository.findById(id).isPresent());

        return id;
    }

}
