package com.emaf.service.repository;

import com.emaf.service.entity.Role;
import com.emaf.service.entity.User;
import com.emaf.service.enumeration.ERole;
import com.emaf.service.enumeration.EUserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByIdAndStatus(String id, EUserStatus status);

    Optional<User> findByEmailAndRole(String email, Role role);

    Optional<User> findByEmail(String email);

    @Query("SELECT eu " +
            "FROM User eu " +
            "WHERE eu.email LIKE CONCAT('%', :email, '%')")
    List<User> findAllByLikeEmail(@Param("email") String email);

    Optional<User> findByEmailAndStatus(String email, EUserStatus status);

    Optional<User> findByEmailAndStatusIn(String email, List<EUserStatus> status);

    List<User> findByRole(Role role);


    boolean existsByEmail(String email);


    User getByEmail(String email);

}