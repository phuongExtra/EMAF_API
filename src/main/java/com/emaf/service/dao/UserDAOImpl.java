package com.emaf.service.dao;

import com.emaf.service.entity.Event;
import com.emaf.service.entity.User;
import com.emaf.service.enumeration.ERole;
import com.emaf.service.enumeration.EUserStatus;
import io.jsonwebtoken.lang.Strings;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * UserDAOImpl
 *
 * @author: VuongVT2
 * @since: 2022/12/03
 */
@Repository
public class UserDAOImpl implements UserDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<User> filterAccount(final String search, final String status, final String role, final long limit, final long offset) {
        String hql = " SELECT u FROM User u ";
        String hqlCondition = " WHERE unaccent(LOWER(CONCAT(u.lastName,' ', u.firstName))) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";
        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND u.status = :status ";
        }
        if (StringUtils.hasLength(role)) {
            hqlCondition += " AND u.role = :role ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);
        Query query = entityManager.createQuery(hqlBuilder.toString(), User.class)
                .setParameter("search", search);
        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EUserStatus.valueOf(status));
        }
        if (StringUtils.hasLength(role)) {
            query.setParameter("role", ERole.valueOf(role));
        }
        return query.setFirstResult((int) offset)
                .setMaxResults((int) limit)
                .getResultList();
    }

    @Override
    public long countFilterAccount(final String search, final String status, final String role) {
        String hql = " SELECT COUNT(*) FROM User u ";
        String hqlCondition = " WHERE unaccent(LOWER(CONCAT(u.lastName,' ', u.firstName))) LIKE unaccent(LOWER(CONCAT('%', :search, '%'))) ";
        if (StringUtils.hasLength(status)) {
            hqlCondition += " AND u.status = :status ";
        }
        if (StringUtils.hasLength(role)) {
            hqlCondition += " AND u.role = :role ";
        }

        StringBuilder hqlBuilder = new StringBuilder("")
                .append(hql)
                .append(hqlCondition);
        Query query = entityManager.createQuery(hqlBuilder.toString(), Long.class)
                .setParameter("search", search);
        if (StringUtils.hasLength(status)) {
            query.setParameter("status", EUserStatus.valueOf(status));
        }
        if (StringUtils.hasLength(role)) {
            query.setParameter("role", ERole.valueOf(role));
        }
        return Long.parseLong(query.getSingleResult().toString());
    }
}
