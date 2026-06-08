package com.flexiwork.repository;

import com.flexiwork.enums.CompanyRole;
import com.flexiwork.model.Company;
import com.flexiwork.model.CompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    Optional<CompanyUser> findByEmail(String email);
    List<CompanyUser> findByCompanyCompanyId(Long companyId);
    List<CompanyUser> findByCompanyAndRole(Company company, CompanyRole role);
    boolean existsByEmail(String email);
}
