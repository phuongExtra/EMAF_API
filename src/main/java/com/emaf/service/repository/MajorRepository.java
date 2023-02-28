package com.emaf.service.repository;

import com.emaf.service.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MajorRepository extends JpaRepository<Major, String>{

}