package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexiwork.enums.ScanType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_verifications")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class QRVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long verificationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Job job;

    @Column(unique = true, length = 255)
    private String qrToken;

    @Enumerated(EnumType.STRING)
    private ScanType scanType;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private Boolean isVerified = false;

    public QRVerification() {}

    public QRVerification(Long verificationId, User user, Job job, String qrToken,
                          ScanType scanType, LocalDateTime checkInTime,
                          LocalDateTime checkOutTime, Boolean isVerified) {
        this.verificationId = verificationId;
        this.user = user;
        this.job = job;
        this.qrToken = qrToken;
        this.scanType = scanType;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.isVerified = isVerified;
    }

    public Long getVerificationId() { return verificationId; }
    public void setVerificationId(Long verificationId) { this.verificationId = verificationId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }

    public ScanType getScanType() { return scanType; }
    public void setScanType(ScanType scanType) { this.scanType = scanType; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long verificationId;
        private User user;
        private Job job;
        private String qrToken;
        private ScanType scanType;
        private LocalDateTime checkInTime;
        private LocalDateTime checkOutTime;
        private Boolean isVerified = false;

        public Builder verificationId(Long val) { this.verificationId = val; return this; }
        public Builder user(User val) { this.user = val; return this; }
        public Builder job(Job val) { this.job = val; return this; }
        public Builder qrToken(String val) { this.qrToken = val; return this; }
        public Builder scanType(ScanType val) { this.scanType = val; return this; }
        public Builder checkInTime(LocalDateTime val) { this.checkInTime = val; return this; }
        public Builder checkOutTime(LocalDateTime val) { this.checkOutTime = val; return this; }
        public Builder isVerified(Boolean val) { this.isVerified = val; return this; }

        public QRVerification build() {
            QRVerification obj = new QRVerification();
            obj.verificationId = this.verificationId;
            obj.user = this.user;
            obj.job = this.job;
            obj.qrToken = this.qrToken;
            obj.scanType = this.scanType;
            obj.checkInTime = this.checkInTime;
            obj.checkOutTime = this.checkOutTime;
            obj.isVerified = this.isVerified;
            return obj;
        }
    }
}
