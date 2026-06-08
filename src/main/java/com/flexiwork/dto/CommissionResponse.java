package com.flexiwork.dto;

import com.flexiwork.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CommissionResponse {
    private Long paymentId;
    private Long companyId;
    private Long jobId;
    private String jobTitle;
    private BigDecimal workerWage;
    private BigDecimal commissionAmount;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public CommissionResponse() {}

    public CommissionResponse(Long paymentId, Long companyId, Long jobId, String jobTitle,
                              BigDecimal workerWage, BigDecimal commissionAmount,
                              PaymentStatus status, LocalDateTime paidAt, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.companyId = companyId;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.workerWage = workerWage;
        this.commissionAmount = commissionAmount;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

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
        private Long companyId;
        private Long jobId;
        private String jobTitle;
        private BigDecimal workerWage;
        private BigDecimal commissionAmount;
        private PaymentStatus status;
        private LocalDateTime paidAt;
        private LocalDateTime createdAt;

        public Builder paymentId(Long val) { this.paymentId = val; return this; }
        public Builder companyId(Long val) { this.companyId = val; return this; }
        public Builder jobId(Long val) { this.jobId = val; return this; }
        public Builder jobTitle(String val) { this.jobTitle = val; return this; }
        public Builder workerWage(BigDecimal val) { this.workerWage = val; return this; }
        public Builder commissionAmount(BigDecimal val) { this.commissionAmount = val; return this; }
        public Builder status(PaymentStatus val) { this.status = val; return this; }
        public Builder paidAt(LocalDateTime val) { this.paidAt = val; return this; }
        public Builder createdAt(LocalDateTime val) { this.createdAt = val; return this; }

        public CommissionResponse build() {
            CommissionResponse obj = new CommissionResponse();
            obj.paymentId = this.paymentId;
            obj.companyId = this.companyId;
            obj.jobId = this.jobId;
            obj.jobTitle = this.jobTitle;
            obj.workerWage = this.workerWage;
            obj.commissionAmount = this.commissionAmount;
            obj.status = this.status;
            obj.paidAt = this.paidAt;
            obj.createdAt = this.createdAt;
            return obj;
        }
    }
}
