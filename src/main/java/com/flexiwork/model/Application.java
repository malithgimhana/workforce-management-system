package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexiwork.enums.ApplicationStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password"})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Job job;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime appliedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime cancelledAt;

    @Column(nullable = false)
    private Boolean lateCancellation = false;

    public Application() {}

    public Application(Long applicationId, User user, Job job, ApplicationStatus status,
                       LocalDateTime appliedAt, LocalDateTime updatedAt) {
        this.applicationId = applicationId;
        this.user = user;
        this.job = job;
        this.status = status;
        this.appliedAt = appliedAt;
        this.updatedAt = updatedAt;
    }

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }
    public Boolean getLateCancellation() { return lateCancellation; }
    public void setLateCancellation(Boolean lateCancellation) { this.lateCancellation = lateCancellation; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long applicationId;
        private User user;
        private Job job;
        private ApplicationStatus status = ApplicationStatus.PENDING;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;

        public Builder applicationId(Long val) { this.applicationId = val; return this; }
        public Builder user(User val) { this.user = val; return this; }
        public Builder job(Job val) { this.job = val; return this; }
        public Builder status(ApplicationStatus val) { this.status = val; return this; }
        public Builder appliedAt(LocalDateTime val) { this.appliedAt = val; return this; }
        public Builder updatedAt(LocalDateTime val) { this.updatedAt = val; return this; }

        public Application build() {
            Application obj = new Application();
            obj.applicationId = this.applicationId;
            obj.user = this.user;
            obj.job = this.job;
            obj.status = this.status;
            obj.appliedAt = this.appliedAt;
            obj.updatedAt = this.updatedAt;
            return obj;
        }
    }
}
