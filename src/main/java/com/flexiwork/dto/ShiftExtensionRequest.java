package com.flexiwork.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class ShiftExtensionRequest {

    @NotBlank(message = "newEndTime is required (HH:mm)")
    private String newEndTime;

    @NotNull(message = "extensionWage is required")
    @DecimalMin(value = "0.01", message = "extensionWage must be positive")
    private BigDecimal extensionWage;

    private String notes = "";

    @NotEmpty(message = "At least one applicationId is required")
    private List<Long> applicationIds;

    public String getNewEndTime() { return newEndTime; }
    public void setNewEndTime(String newEndTime) { this.newEndTime = newEndTime; }

    public BigDecimal getExtensionWage() { return extensionWage; }
    public void setExtensionWage(BigDecimal extensionWage) { this.extensionWage = extensionWage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes != null ? notes : ""; }

    public List<Long> getApplicationIds() { return applicationIds; }
    public void setApplicationIds(List<Long> applicationIds) { this.applicationIds = applicationIds; }
}
