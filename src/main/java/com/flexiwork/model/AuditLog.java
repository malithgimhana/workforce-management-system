package com.flexiwork.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_user", columnList = "user_id"),
    @Index(name = "idx_audit_action", columnList = "action"),
    @Index(name = "idx_audit_created", columnList = "created_at")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 20)
    private String userType;   // WORKER, EMPLOYER, ADMIN, SECURITY

    @Column(length = 100, nullable = false)
    private String action;     // LOGIN, LOGOUT, JOB_CREATED, APP_APPROVED, PAYMENT_MADE …

    @Column(length = 50)
    private String entityType; // User, Job, Application, Payment …

    private Long entityId;

    @Column(columnDefinition = "TEXT")
    private String details;    // free-form JSON / message

    @Column(length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public AuditLog() {}

    // ── Getters / Setters ──────────────────────────────────────
    public Long getLogId() { return logId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
