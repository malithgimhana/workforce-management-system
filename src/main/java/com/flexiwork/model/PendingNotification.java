package com.flexiwork.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "pending_notifications")
public class PendingNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notifId;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(columnDefinition = "LONGTEXT")
    private String qrBase64;

    @Column(nullable = false)
    private String endpoint = "/send"; // /send, /checkin, /checkout, /send-qr

    @Column(nullable = false)
    private Integer retryCount = 0;

    @Column(nullable = false, length = 10)
    private String status = "PENDING"; // PENDING, SENT, FAILED

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    public PendingNotification() {}

    public Long getNotifId() { return notifId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getQrBase64() { return qrBase64; }
    public void setQrBase64(String qrBase64) { this.qrBase64 = qrBase64; }
    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
