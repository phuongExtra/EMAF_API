package com.emaf.service.dao;

import com.emaf.service.entity.Event;
import com.emaf.service.enumeration.EEventStatus;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * ReportDAOImpl
 *
 * @author: VuongVT2
 * @since: 2023/01/09
 */
@Repository
public class ReportDAOImpl implements ReportDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Long numberOfEventBy(final String fromDate, final String toDate, final List<EEventStatus> statuses) {
        String hql = "SELECT COUNT(*) FROM Event e WHERE 1 = 1 ";
        if (StringUtils.hasLength(fromDate)) {
            hql += " e.startTime >= :fromDate ";
        }
        if (StringUtils.hasLength(toDate)) {
            hql += " e.startTime <= :toDate ";
        }
        if (!CollectionUtils.isEmpty(statuses)) {
            hql += " e.status IN (:statuses) ";
        }

        Query query = entityManager.createQuery(hql, Long.class);
        if (StringUtils.hasLength(fromDate)) {
            query.setParameter("fromDate", fromDate);
        }
        if (StringUtils.hasLength(toDate)) {
            query.setParameter("toDate", toDate);
        }
        if (!CollectionUtils.isEmpty(statuses)) {
            query.setParameter("statuses", statuses);
        }
        return Long.parseLong(query.getSingleResult().toString());
    }
}
