package com.flexiwork.repository;

import com.flexiwork.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:action   IS NULL OR a.action   = :action)
          AND (:userId   IS NULL OR a.userId   = :userId)
          AND (:userType IS NULL OR a.userType = :userType)
          AND (:from     IS NULL OR a.createdAt >= :from)
          AND (:to       IS NULL OR a.createdAt <= :to)
        ORDER BY a.createdAt DESC
    """)
    Page<AuditLog> search(
        @Param("action")   String action,
        @Param("userId")   Long userId,
        @Param("userType") String userType,
        @Param("from")     LocalDateTime from,
        @Param("to")       LocalDateTime to,
        Pageable pageable
    );
}
