package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.enums.Gender;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 60)
    private String firstName;

    @Column(nullable = false, length = 60)
    private String lastName;

    @Column(unique = true, nullable = false, length = 20)
    private String nic;

    @Column(unique = true, nullable = false, length = 15)
    private String phone;

    @Column(unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 50)
    private String district;

    @Column(length = 255)
    private String photo;

    // Document verification
    @Column(length = 255)
    private String nicFrontPath;

    @Column(length = 255)
    private String nicBackPath;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DocumentStatus docStatus = DocumentStatus.NOT_SUBMITTED;

    @Column(length = 500)
    private String docRejectReason;

    @Column
    private LocalDateTime docSubmittedAt;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public User() {}

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getNicFrontPath() { return nicFrontPath; }
    public void setNicFrontPath(String nicFrontPath) { this.nicFrontPath = nicFrontPath; }
    public String getNicBackPath() { return nicBackPath; }
    public void setNicBackPath(String nicBackPath) { this.nicBackPath = nicBackPath; }
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

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long userId;
        private String firstName;
        private String lastName;
        private String nic;
        private String phone;
        private String email;
        private Gender gender;
        private String address;
        private String district;
        private String photo;
        private String password;
        private Boolean isDeleted = false;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder userId(Long val) { this.userId = val; return this; }
        public Builder firstName(String val) { this.firstName = val; return this; }
        public Builder lastName(String val) { this.lastName = val; return this; }
        public Builder nic(String val) { this.nic = val; return this; }
        public Builder phone(String val) { this.phone = val; return this; }
        public Builder email(String val) { this.email = val; return this; }
        public Builder gender(Gender val) { this.gender = val; return this; }
        public Builder address(String val) { this.address = val; return this; }
        public Builder district(String val) { this.district = val; return this; }
        public Builder photo(String val) { this.photo = val; return this; }
        public Builder password(String val) { this.password = val; return this; }
        public Builder isDeleted(Boolean val) { this.isDeleted = val; return this; }
        public Builder createdAt(LocalDateTime val) { this.createdAt = val; return this; }
        public Builder updatedAt(LocalDateTime val) { this.updatedAt = val; return this; }

        public User build() {
            User obj = new User();
            obj.userId = this.userId;
            obj.firstName = this.firstName;
            obj.lastName = this.lastName;
            obj.nic = this.nic;
            obj.phone = this.phone;
            obj.email = this.email;
            obj.gender = this.gender;
            obj.address = this.address;
            obj.district = this.district;
            obj.photo = this.photo;
            obj.password = this.password;
            obj.isDeleted = this.isDeleted;
            obj.createdAt = this.createdAt;
            obj.updatedAt = this.updatedAt;
            return obj;
        }
    }
}
