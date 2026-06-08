package com.flexiwork.repository;

import com.flexiwork.enums.ApplicationStatus;
import com.flexiwork.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Optional<Application> findByUserUserIdAndJobJobId(Long userId, Long jobId);
    List<Application> findByJobJobId(Long jobId);
    List<Application> findByUserUserId(Long userId);
    long countByJobJobIdAndStatus(Long jobId, ApplicationStatus status);
    List<Application> findByJobCompanyCompanyIdAndStatus(Long companyId, ApplicationStatus status);
    List<Application> findByJobJobIdAndStatusIn(Long jobId, List<ApplicationStatus> statuses);
}
