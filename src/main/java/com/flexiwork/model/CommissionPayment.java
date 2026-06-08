package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexiwork.enums.PaymentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commission_payments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CommissionPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "balance"})
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Job job;

    @Column(precision = 10, scale = 2)
    private BigDecimal workerWage;

    @Column(precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status = PaymentStatus.PENDING;

    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public CommissionPayment() {}

    public CommissionPayment(Long paymentId, Company company, Job job, BigDecimal workerWage,
                             BigDecimal commissionAmount, PaymentStatus status,
                             LocalDateTime paidAt, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.company = company;
        this.job = job;
        this.workerWage = workerWage;
        this.commissionAmount = commissionAmount;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public BigDecimal getWorkerWage() { return workerWage; }
    public void setWorkerWage(BigDecimal workerWage) { this.workerWage = workerWage; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long paymentId;
        private Company company;
        private Job job;
        private BigDecimal workerWage;
        private BigDecimal commissionAmount;
        private PaymentStatus status = PaymentStatus.PENDING;
        private LocalDateTime paidAt;
        private LocalDateTime createdAt;

        public Builder paymentId(Long val) { this.paymentId = val; return this; }
        public Builder company(Company val) { this.company = val; return this; }
        public Builder job(Job val) { this.job = val; return this; }
        public Builder workerWage(BigDecimal val) { this.workerWage = val; return this; }
        public Builder commissionAmount(BigDecimal val) { this.commissionAmount = val; return this; }
        public Builder status(PaymentStatus val) { this.status = val; return this; }
        public Builder paidAt(LocalDateTime val) { this.paidAt = val; return this; }
        public Builder createdAt(LocalDateTime val) { this.createdAt = val; return this; }

        public CommissionPayment build() {
            CommissionPayment obj = new CommissionPayment();
            obj.paymentId = this.paymentId;
            obj.company = this.company;
            obj.job = this.job;
            obj.workerWage = this.workerWage;
            obj.commissionAmount = this.commissionAmount;
            obj.status = this.status;
            obj.paidAt = this.paidAt;
            obj.createdAt = this.createdAt;
            return obj;
        }
    }
}
