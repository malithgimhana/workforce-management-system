package com.flexiwork.dto;

import com.flexiwork.enums.Gender;
import com.flexiwork.enums.JobCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class JobPostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Daily wage is required")
    @DecimalMin(value = "1500.00", message = "Daily wage must be at least LKR 1,500")
    private BigDecimal dailyWage;

    @NotNull(message = "Shift start time is required")
    private LocalTime shiftStartTime;

    @NotNull(message = "Shift end time is required")
    private LocalTime shiftEndTime;

    @NotNull(message = "Required workers is required")
    @Min(value = 1, message = "At least 1 worker required")
    private Integer requiredWorkers;

    @NotBlank(message = "Factory location is required")
    private String factoryLocation;

    @NotBlank(message = "District is required")
    private String district;

    private Double latitude;

    private Double longitude;

    private Gender gender;

    private Integer minAge;

    private Integer maxAge;

    @NotNull(message = "Category is required")
    private JobCategory category;

    @NotNull(message = "Shift date is required")
    @FutureOrPresent(message = "Shift date must be today or in the future")
    private LocalDate shiftDate;

    public JobPostRequest() {}

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

    public LocalDate getShiftDate() { return shiftDate; }
    public void setShiftDate(LocalDate shiftDate) { this.shiftDate = shiftDate; }
}
