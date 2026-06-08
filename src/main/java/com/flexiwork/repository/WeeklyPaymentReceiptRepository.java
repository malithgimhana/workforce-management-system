package com.flexiwork.repository;

import com.flexiwork.model.WeeklyPaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyPaymentReceiptRepository extends JpaRepository<WeeklyPaymentReceipt, Long> {
    Optional<WeeklyPaymentReceipt> findByCompanyCompanyIdAndWeekStart(Long companyId, LocalDate weekStart);
    List<WeeklyPaymentReceipt> findByCompanyCompanyIdOrderByWeekStartDesc(Long companyId);
    List<WeeklyPaymentReceipt> findAllByOrderByCreatedAtDesc();
}
