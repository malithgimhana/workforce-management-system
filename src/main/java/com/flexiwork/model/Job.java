package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexiwork.enums.Gender;
import com.flexiwork.enums.JobCategory;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "jobs")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "balance"})
    private Company company;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal dailyWage;

    private LocalTime shiftStartTime;

    private LocalTime shiftEndTime;

    private Integer requiredWorkers;

    private Integer approvedWorkers = 0;

    @Column(length = 255)
    private String factoryLocation;

    @Column(length = 50)
    private String district;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.ANY;

    private Integer minAge;

    private Integer maxAge;

    @Enumerated(EnumType.STRING)
    private JobCategory category;

    private Boolean isActive = true;

    private Boolean isDeleted = false;

    private LocalDate shiftDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public Job() {}

    public Job(Long jobId, Company company, String title, String description, BigDecimal dailyWage,
               LocalTime shiftStartTime, LocalTime shiftEndTime, Integer requiredWorkers,
               Integer approvedWorkers, String factoryLocation, Double latitude, Double longitude,
               Gender gender, Integer minAge, Integer maxAge, JobCategory category,
               Boolean isActive, Boolean isDeleted, LocalDate shiftDate,
               LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.jobId = jobId;
        this.company = company;
        this.title = title;
        this.description = description;
        this.dailyWage = dailyWage;
        this.shiftStartTime = shiftStartTime;
        this.shiftEndTime = shiftEndTime;
        this.requiredWorkers = requiredWorkers;
        this.approvedWorkers = approvedWorkers;
        this.factoryLocation = factoryLocation;
        this.latitude = latitude;
        this.longitude = longitude;
        this.gender = gender;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.category = category;
        this.isActive = isActive;
        this.isDeleted = isDeleted;
        this.shiftDate = shiftDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public Long getJobId() { return jobId; }
    public void setJobId(Long jobId) { this.jobId = jobId; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getDailyWage() { return dailyWage; }
    public void setDailyWage(BigDecimal dailyWage) { this.dailyWage = dailyWage; }

    public LocalTime getShiftStartTime() { return shiftStartTime; }
    public void setShiftStartTime(LocalTime shiftStartTime) { this.shiftStartTime = shiftStartTime; }

    public LocalTime getShiftEndTime() { return shiftEndTime; }
    public void setShiftEndTime(LocalTime shiftEndTime) { this.shiftEndTime = shiftEndTime; }

    public Integer getRequiredWorkers() { return requiredWorkers; }
    public void setRequiredWorkers(Integer requiredWorkers) { this.requiredWorkers = requiredWorkers; }

    public Integer getApprovedWorkers() { return approvedWorkers; }
    public void setApprovedWorkers(Integer approvedWorkers) { this.approvedWorkers = approvedWorkers; }

    public String getFactoryLocation() { return factoryLocation; }
    public void setFactoryLocation(String factoryLocation) { this.factoryLocation = factoryLocation; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }

    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }

    public JobCategory getCategory() { return category; }
    public void setCategory(JobCategory category) { this.category = category; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long jobId;
        private Company company;
        private String title;
        private String description;
        private BigDecimal dailyWage;
        private LocalTime shiftStartTime;
        private LocalTime shiftEndTime;
        private Integer requiredWorkers;
        private Integer approvedWorkers = 0;
        private String factoryLocation;
        private String district;
        private Double latitude;
        private Double longitude;
        private Gender gender = Gender.ANY;
        private Integer minAge;
        private Integer maxAge;
        private JobCategory category;
        private Boolean isActive = true;
        private Boolean isDeleted = false;
        private LocalDate shiftDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime deletedAt;

        public Builder jobId(Long val) { this.jobId = val; return this; }
        public Builder company(Company val) { this.company = val; return this; }
        public Builder title(String val) { this.title = val; return this; }
        public Builder description(String val) { this.description = val; return this; }
        public Builder dailyWage(BigDecimal val) { this.dailyWage = val; return this; }
        public Builder shiftStartTime(LocalTime val) { this.shiftStartTime = val; return this; }
        public Builder shiftEndTime(LocalTime val) { this.shiftEndTime = val; return this; }
        public Builder requiredWorkers(Integer val) { this.requiredWorkers = val; return this; }
        public Builder approvedWorkers(Integer val) { this.approvedWorkers = val; return this; }
        public Builder factoryLocation(String val) { this.factoryLocation = val; return this; }
        public Builder district(String val) { this.district = val; return this; }
        public Builder latitude(Double val) { this.latitude = val; return this; }
        public Builder longitude(Double val) { this.longitude = val; return this; }
        public Builder gender(Gender val) { this.gender = val; return this; }
        public Builder minAge(Integer val) { this.minAge = val; return this; }
        public Builder maxAge(Integer val) { this.maxAge = val; return this; }
        public Builder category(JobCategory val) { this.category = val; return this; }
        public Builder isActive(Boolean val) { this.isActive = val; return this; }
        public Builder isDeleted(Boolean val) { this.isDeleted = val; return this; }
        public Builder shiftDate(LocalDate val) { this.shiftDate = val; return this; }
        public Builder createdAt(LocalDateTime val) { this.createdAt = val; return this; }
        public Builder updatedAt(LocalDateTime val) { this.updatedAt = val; return this; }
        public Builder deletedAt(LocalDateTime val) { this.deletedAt = val; return this; }

        public Job build() {
            Job obj = new Job();
            obj.jobId = this.jobId;
            obj.company = this.company;
            obj.title = this.title;
            obj.description = this.description;
            obj.dailyWage = this.dailyWage;
            obj.shiftStartTime = this.shiftStartTime;
            obj.shiftEndTime = this.shiftEndTime;
            obj.requiredWorkers = this.requiredWorkers;
            obj.approvedWorkers = this.approvedWorkers;
            obj.factoryLocation = this.factoryLocation;
            obj.district = this.district;
            obj.latitude = this.latitude;
            obj.longitude = this.longitude;
            obj.gender = this.gender;
            obj.minAge = this.minAge;
            obj.maxAge = this.maxAge;
            obj.category = this.category;
            obj.isActive = this.isActive;
            obj.isDeleted = this.isDeleted;
            obj.shiftDate = this.shiftDate;
            obj.createdAt = this.createdAt;
            obj.updatedAt = this.updatedAt;
            obj.deletedAt = this.deletedAt;
            return obj;
        }
    }
}
