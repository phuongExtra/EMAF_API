package com.emaf.service.dao;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventConfiguration;
import com.emaf.service.enumeration.EEventConfigurationStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * EventConfigurationDAOImpl
 *
 * @author: PhuongLN
 * @since: 2023/01/04
 */
@Repository
public class EventConfigurationDAOImpl implements EventConfigurationDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EventConfiguration> filterEventConfigs(final long limit, final long offset, final String status) {
        String hql = " SELECT ec FROM EventConfiguration ec ";

        String hqlCondition = "";

        if (!status.isBlank()) {
            hqlCondition += " WHERE e.status = :status ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        hqlBuilder.append("ORDER BY ec.updatedAt DESC");

        Query query = entityManager.createQuery(hqlBuilder.toString(), EventConfiguration.class);

        if(!status.isBlank()){
            query.setParameter("status", EEventConfigurationStatus.valueOf(status));
        }

        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    public long countEventConfigs(String status){
        String hql = " SELECT COUNT ec FROM EventConfiguration ec ";

        String hqlCondition = "";

        if (!status.isBlank()) {
            hqlCondition += " WHERE e.status = :status ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);


        Query query = entityManager.createQuery(hqlBuilder.toString(), EventConfiguration.class);

        if(!status.isBlank()){
            query.setParameter("status", EEventConfigurationStatus.valueOf(status));
        }

        return Long.parseLong(query.getSingleResult().toString());
    }
}