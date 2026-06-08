package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "weekly_payment_receipts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WeeklyPaymentReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    @JsonIgnoreProperties({"password", "balance", "brCertPath", "docRejectReason"})
    private Company company;

    @Column(nullable = false)
    private LocalDate weekStart; // Always Monday

    @Column(nullable = false)
    private LocalDate weekEnd;   // Always Sunday

    @Column(precision = 14, scale = 2)
    private BigDecimal totalWorkerWages = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal commissionRate = new BigDecimal("10.00"); // 10%

    @Column(precision = 14, scale = 2)
    private BigDecimal totalCommission = BigDecimal.ZERO;

    @Column(length = 10)
    private String paymentStatus = "PENDING"; // PENDING, PAID

    @Column(length = 4)
    private String cardLast4;

    private LocalDateTime paidAt;

    // Line items (jobs in this period)
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "receipt_commission_payments",
        joinColumns = @JoinColumn(name = "receipt_id"),
        inverseJoinColumns = @JoinColumn(name = "payment_id")
    )
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<CommissionPayment> lineItems;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public WeeklyPaymentReceipt() {}

    // ── Getters / Setters ─────────────────────────────────────────────────
    public Long getReceiptId() { return receiptId; }
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }
    public LocalDate getWeekEnd() { return weekEnd; }
    public void setWeekEnd(LocalDate weekEnd) { this.weekEnd = weekEnd; }
    public BigDecimal getTotalWorkerWages() { return totalWorkerWages; }
    public void setTotalWorkerWages(BigDecimal v) { this.totalWorkerWages = v; }
    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal v) { this.commissionRate = v; }
    public BigDecimal getTotalCommission() { return totalCommission; }
    public void setTotalCommission(BigDecimal v) { this.totalCommission = v; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getCardLast4() { return cardLast4; }
    public void setCardLast4(String cardLast4) { this.cardLast4 = cardLast4; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    public List<CommissionPayment> getLineItems() { return lineItems; }
    public void setLineItems(List<CommissionPayment> lineItems) { this.lineItems = lineItems; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
