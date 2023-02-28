package com.emaf.service.dao;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.EventParticipation;
import com.emaf.service.enumeration.EEventParticipationStatus;
import com.emaf.service.enumeration.EEventStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * EventParticipationDAOImpl
 *
 * @author: VuongVT2
 * @since: 2022/11/09
 */
@Repository
public class EventParticipationDAOImpl implements EventParticipationDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<EventParticipation> filterEventParticipant(final String eventId, final String search, final String status, final long limit, final long offset) {
        String hql = "SELECT ep FROM EventParticipation ep  ";
        String hqlCondition = " WHERE ep.event.id = :eventId ";
        if (StringUtils.hasLength(search)) {
            hql += " LEFT JOIN User u ON ep.user.id = u.id ";
            hqlCondition += " AND unaccent(LOWER(CONCAT(u.lastName,' ', u.firstName))) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";
        }
        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND ep.status = :status ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append("ORDER BY ep.id");

        Query query = entityManager.createQuery(hqlBuilder.toString(), EventParticipation.class)
                .setParameter("eventId", eventId);
        if (StringUtils.hasLength(search)) {
            query.setParameter("search", search);
        }

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EEventParticipationStatus.valueOf(status));
        }
        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countfilterEventParticipant(final String eventId, final String search, final String status) {
        String hql = "SELECT count (distinct(ep)) FROM EventParticipation ep ";
        String hqlCondition = " WHERE ep.event.id = :eventId ";
        if (StringUtils.hasLength(search)) {
            hql += " LEFT JOIN User u ON ep.user.id = u.id ";
            hqlCondition += " AND unaccent(LOWER(CONCAT(u.lastName,' ', u.firstName))) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";
        }
        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND ep.status = :status ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("eventId", eventId);
        if (StringUtils.hasLength(search)) {
            query.setParameter("search", search);
        }

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EEventParticipationStatus.valueOf(status));
        }
        return Long.parseLong(query.getSingleResult().toString());
    }

}
