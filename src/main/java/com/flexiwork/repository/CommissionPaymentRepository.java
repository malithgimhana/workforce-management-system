package com.flexiwork.repository;

import com.flexiwork.model.CommissionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommissionPaymentRepository extends JpaRepository<CommissionPayment, Long> {
    List<CommissionPayment> findByCompanyCompanyId(Long companyId);
    List<CommissionPayment> findByJobJobId(Long jobId);

    @Query("SELECT COALESCE(SUM(cp.commissionAmount), 0) FROM CommissionPayment cp WHERE cp.company.companyId = :companyId")
    BigDecimal sumCommissionByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT COALESCE(SUM(cp.commissionAmount), 0) FROM CommissionPayment cp WHERE cp.createdAt >= :from")
    BigDecimal sumCommissionSince(@Param("from") LocalDateTime from);

    @Query("SELECT cp FROM CommissionPayment cp ORDER BY cp.createdAt DESC")
    List<CommissionPayment> findRecentPayments(org.springframework.data.domain.Pageable pageable);
}
