package com.flexiwork.repository;

import com.flexiwork.model.QRVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QRVerificationRepository extends JpaRepository<QRVerification, Long> {
    Optional<QRVerification> findByQrToken(String qrToken);
    Optional<QRVerification> findByUserUserIdAndJobJobId(Long userId, Long jobId);
    List<QRVerification> findByJobJobId(Long jobId);
    List<QRVerification> findByJobCompanyCompanyId(Long companyId);
}
