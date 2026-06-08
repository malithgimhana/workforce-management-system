package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexiwork.enums.CompanyRole;
import jakarta.persistence.*;

@Entity
@Table(name = "company_users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CompanyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyUserId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "balance"})
    private Company company;

    @Column(length = 100)
    private String name;

    @Column(unique = true, length = 100)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private CompanyRole role;

    public CompanyUser() {}

    public CompanyUser(Long companyUserId, Company company, String name, String email,
                       String password, CompanyRole role) {
        this.companyUserId = companyUserId;
        this.company = company;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getCompanyUserId() { return companyUserId; }
    public void setCompanyUserId(Long companyUserId) { this.companyUserId = companyUserId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public CompanyRole getRole() { return role; }
    public void setRole(CompanyRole role) { this.role = role; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long companyUserId;
        private Company company;
        private String name;
        private String email;
        private String password;
        private CompanyRole role;

        public Builder companyUserId(Long val) { this.companyUserId = val; return this; }
        public Builder company(Company val) { this.company = val; return this; }
        public Builder name(String val) { this.name = val; return this; }
        public Builder email(String val) { this.email = val; return this; }
        public Builder password(String val) { this.password = val; return this; }
        public Builder role(CompanyRole val) { this.role = val; return this; }

        public CompanyUser build() {
            CompanyUser obj = new CompanyUser();
            obj.companyUserId = this.companyUserId;
            obj.company = this.company;
            obj.name = this.name;
            obj.email = this.email;
            obj.password = this.password;
            obj.role = this.role;
            return obj;
        }
    }
}
