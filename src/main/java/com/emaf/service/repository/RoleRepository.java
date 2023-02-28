package com.emaf.service.repository;

import com.emaf.service.entity.Role;
import com.emaf.service.enumeration.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, ERole> {

}