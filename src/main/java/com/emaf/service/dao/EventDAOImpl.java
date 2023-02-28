package com.emaf.service.dao;

import com.emaf.service.entity.Event;
import com.emaf.service.enumeration.*;
import io.jsonwebtoken.lang.Strings;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * EventDAOImpl
 *
 * @author: VuongVT2
 * @since: 2022/10/24
 */
@Repository
public class EventDAOImpl implements EventDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Event> filterEvent(final boolean isManaged, final String search, final List<EEventStatus> statuses, final String type, final long limit, final long offset) {
        String hql = " SELECT e FROM Event e ";
        String hqlCondition = " WHERE unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%')))";
        if (!CollectionUtils.isEmpty(statuses)) {
            hqlCondition += " AND e.status IN (:status) ";
        }
        if (StringUtils.hasLength(type)) {
            hqlCondition += " AND e.type = :type ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);
        if (isManaged) {
            hqlBuilder.append("ORDER BY e.createdAt DESC");
        } else {
            hqlBuilder.append("ORDER BY e.startTime");
        }

        Query query = entityManager.createQuery(hqlBuilder.toString(), Event.class)
                .setParameter("search", search);

        if (!CollectionUtils.isEmpty(statuses)) {
            query.setParameter("status", statuses);
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countFilterEvent(final String search, final List<EEventStatus> statuses, final String type) {
        String hql = " SELECT COUNT(e.id) FROM Event e ";
        String hqlCondition = " WHERE unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";
        if (!CollectionUtils.isEmpty(statuses)) {
            hqlCondition += " AND e.status IN (:status) ";
        }
        if (StringUtils.hasLength(type)) {
            hqlCondition += " AND e.type = :type ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search);

        if (!CollectionUtils.isEmpty(statuses)) {
            query.setParameter("status", statuses);
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return Long.parseLong(query.getSingleResult().toString());
    }

    @Override
    public List<Event> filterAllMyEvents(final String userId, final String search, final String status, final String type, final long limit, final long offset) {
        String hql = "SELECT DISTINCT e FROM Event e LEFT JOIN OrganizationCommittee uer " +
                " ON e.id = uer.event.id  ";

        String hqlCondition = " WHERE uer.user.id = :userId AND unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%')))";
        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND e.status = :status ";
        }
        if (StringUtils.hasLength(type)) {
            hqlCondition += " AND e.type = :type ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append("ORDER BY e.updatedAt DESC");

        Query query = entityManager.createQuery(hqlBuilder.toString(), Event.class)
                .setParameter("search", search);

        query.setParameter("userId", userId);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EEventStatus.valueOf(status));
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();

    }

    @Override
    public long countAllMyEvent(final String userId, final String search, final String status, final String type) {
        String hql = "SELECT COUNT(e.id) FROM Event e LEFT JOIN OrganizationCommittee uer " +
                " ON e.id = uer.event.id ";

        String hqlCondition = " WHERE uer.user.id = :userId AND unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%')))";
        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND e.status = :status ";
        }
        if (StringUtils.hasLength(type)) {
            hqlCondition += " AND e.type = :type ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search);

        query.setParameter("userId", userId);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EEventStatus.valueOf(status));
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return Long.parseLong(query.getSingleResult().toString());
    }

    @Override
    public List<Event> filterAllManagedEvent(final String userId, final String search, final String status, final String type, final long limit, final long offset) {
        String hql = "SELECT DISTINCT e FROM Event e  WHERE e.createdBy = :userId " +
                " AND unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";

        if (StringUtils.hasLength(status)) {
            hql += " AND e.status = :status ";
        }
        if (StringUtils.hasLength(type)) {
            hql += " AND e.type = :type ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append("ORDER BY e.startTime");

        Query query = entityManager.createQuery(hqlBuilder.toString(), Event.class)
                .setParameter("search", search);

        query.setParameter("userId", userId);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EEventStatus.valueOf(status));
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countAllManagedEvent(final String userId, final String search, final String status, final String type) {
        String hql = "SELECT count(e.id) FROM Event e  WHERE e.createdBy = :userId " +
                " AND unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";

        if (StringUtils.hasLength(status)) {
            hql += " AND e.status = :status ";
        }
        if (StringUtils.hasLength(type)) {
            hql += " AND e.type = :type ";
        }
        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search);

        query.setParameter("userId", userId);

        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EEventStatus.valueOf(status));
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return Long.parseLong(query.getSingleResult().toString());
    }

    @Override
    public List<Event> filterEventsBy(final String userId, ERole role, EOrganizationCommitteeStatus committeeStatus, final String search, final List<EEventStatus> statuses, final String type, final long limit, final long offset) {
        String hql = " SELECT e FROM OrganizationCommittee oc LEFT JOIN Event e ON oc.event.id = e.id ";
        String hqlCondition = " WHERE unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) " +
                " AND oc.user.id = :userId AND oc.role.id = :roleId AND oc.status = :committeeStatus ";
        if (!CollectionUtils.isEmpty(statuses)) {
            hqlCondition += " AND e.status IN (:status) ";
        }
        if (StringUtils.hasLength(type)) {
            hqlCondition += " AND e.type = :type ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append(" ORDER BY e.startTime");

        Query query = entityManager.createQuery(hqlBuilder.toString(), Event.class)
                .setParameter("search", search)
                .setParameter("userId", userId)
                .setParameter("roleId", role)
                .setParameter("committeeStatus", committeeStatus);

        if (!CollectionUtils.isEmpty(statuses)) {
            query.setParameter("status", statuses);
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countfilterEventsBy(final String userId, ERole role, EOrganizationCommitteeStatus committeeStatus, final String search, final List<EEventStatus> statuses, final String type) {
        String hql = " SELECT count(*) FROM OrganizationCommittee oc LEFT JOIN Event e ON oc.event.id = e.id ";
        String hqlCondition = " WHERE unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) " +
                "AND oc.user.id = :userId AND oc.role.id = :roleId AND oc.status = :committeeStatus";
        if (!CollectionUtils.isEmpty(statuses)) {
            hqlCondition += " AND e.status IN (:status) ";
        }
        if (StringUtils.hasLength(type)) {
            hqlCondition += " AND e.type = :type ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search)
                .setParameter("userId", userId)
                .setParameter("roleId", role)
                .setParameter("committeeStatus", committeeStatus);

        if (!CollectionUtils.isEmpty(statuses)) {
            query.setParameter("status", statuses);
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return Long.parseLong(query.getSingleResult().toString());
    }

    @Override
    public List<Event> filterParticipatingEvents(final String userId, List<EEventParticipationStatus> participationStatuses, final String search, final List<EEventStatus> statuses, final String type, final long limit, final long offset) {
        String hql = " SELECT e FROM EventParticipation ep LEFT JOIN Event e ON ep.event.id = e.id ";
        String hqlCondition = " WHERE unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) " +
                " AND ep.user.id = :userId AND ep.status IN (:participationStatuses) ";
        if (!CollectionUtils.isEmpty(statuses)) {
            hqlCondition += " AND e.status IN (:status) ";
        }
        if (StringUtils.hasLength(type)) {
            hqlCondition += " AND e.type = :type ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition)
                .append("ORDER BY e.startTime ");

        Query query = entityManager.createQuery(hqlBuilder.toString(), Event.class)
                .setParameter("search", search)
                .setParameter("userId", userId)
                .setParameter("participationStatuses", participationStatuses);

        if (!CollectionUtils.isEmpty(statuses)) {
            query.setParameter("status", statuses);
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countParticipatingEvents(final String userId, final List<EEventParticipationStatus> participationStatuses, final String search, final List<EEventStatus> statuses, final String type) {
        String hql = " SELECT count(*) FROM EventParticipation ep LEFT JOIN Event e ON ep.event.id = e.id ";
        String hqlCondition = " WHERE unaccent(LOWER(e.eventName)) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) " +
                " AND ep.user.id = :userId AND ep.status IN (:participationStatuses) ";
        if (!CollectionUtils.isEmpty(statuses)) {
            hqlCondition += " AND e.status IN (:status) ";
        }
        if (StringUtils.hasLength(type)) {
            hqlCondition += " AND e.type = :type ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);

        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search)
                .setParameter("userId", userId)
                .setParameter("participationStatuses", participationStatuses);

        if (!CollectionUtils.isEmpty(statuses)) {
            query.setParameter("status", statuses);
        }
        if (StringUtils.hasLength(type)) {
            query.setParameter("type", EEventType.valueOf(type));
        }
        return Long.parseLong(query.getSingleResult().toString());
    }

}
