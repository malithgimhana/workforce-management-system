package com.flexiwork.repository;

import com.flexiwork.model.Company;
import com.flexiwork.model.SecurityGuard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SecurityGuardRepository extends JpaRepository<SecurityGuard, Long> {
    Optional<SecurityGuard> findByEmail(String email);
    List<SecurityGuard> findByCompanyCompanyId(Long companyId);
    List<SecurityGuard> findByCompany(Company company);
    List<SecurityGuard> findByIsActiveTrue();
}
