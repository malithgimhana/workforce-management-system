package com.flexiwork.repository;

import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByEmail(String email);
    Optional<Company> findByBrNumber(String brNumber);
    boolean existsByEmail(String email);
    boolean existsByBrNumber(String brNumber);
    List<Company> findByDocStatus(DocumentStatus docStatus);
    List<Company> findByDocStatusAndDocSubmittedAtBefore(DocumentStatus docStatus, LocalDateTime cutoff);
}
