package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexiwork.enums.DocumentStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "companies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    @Column(length = 100)
    private String name;

    @Column(unique = true, length = 50)
    private String brNumber;

    @Column(length = 15)
    private String phone;

    @Column(unique = true, length = 100)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    // Document verification
    @Column(length = 255)
    private String brCertPath;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DocumentStatus docStatus = DocumentStatus.NOT_SUBMITTED;

    @Column(length = 500)
    private String docRejectReason;

    @Column
    private LocalDateTime docSubmittedAt;

    @JsonIgnore
    private String password;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Company() {}

    public Company(Long companyId, String name, String brNumber, String phone, String email,
                   String address, BigDecimal balance, String password, Boolean isDeleted,
                   LocalDateTime createdAt) {
        this.companyId = companyId;
        this.name = name;
        this.brNumber = brNumber;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.balance = balance;
        this.password = password;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
    }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrNumber() { return brNumber; }
    public void setBrNumber(String brNumber) { this.brNumber = brNumber; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getBrCertPath() { return brCertPath; }
    public void setBrCertPath(String brCertPath) { this.brCertPath = brCertPath; }
    public DocumentStatus getDocStatus() { return docStatus; }
    public void setDocStatus(DocumentStatus docStatus) { this.docStatus = docStatus; }
    public String getDocRejectReason() { return docRejectReason; }
    public void setDocRejectReason(String docRejectReason) { this.docRejectReason = docRejectReason; }
    public LocalDateTime getDocSubmittedAt() { return docSubmittedAt; }
    public void setDocSubmittedAt(LocalDateTime docSubmittedAt) { this.docSubmittedAt = docSubmittedAt; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long companyId;
        private String name;
        private String brNumber;
        private String phone;
        private String email;
        private String address;
        private BigDecimal balance = BigDecimal.ZERO;
        private String password;
        private Boolean isDeleted = false;
        private LocalDateTime createdAt;

        public Builder companyId(Long val) { this.companyId = val; return this; }
        public Builder name(String val) { this.name = val; return this; }
        public Builder brNumber(String val) { this.brNumber = val; return this; }
        public Builder phone(String val) { this.phone = val; return this; }
        public Builder email(String val) { this.email = val; return this; }
        public Builder address(String val) { this.address = val; return this; }
        public Builder balance(BigDecimal val) { this.balance = val; return this; }
        public Builder password(String val) { this.password = val; return this; }
        public Builder isDeleted(Boolean val) { this.isDeleted = val; return this; }
        public Builder createdAt(LocalDateTime val) { this.createdAt = val; return this; }

        public Company build() {
            Company obj = new Company();
            obj.companyId = this.companyId;
            obj.name = this.name;
            obj.brNumber = this.brNumber;
            obj.phone = this.phone;
            obj.email = this.email;
            obj.address = this.address;
            obj.balance = this.balance;
            obj.password = this.password;
            obj.isDeleted = this.isDeleted;
            obj.createdAt = this.createdAt;
            return obj;
        }
    }
}
