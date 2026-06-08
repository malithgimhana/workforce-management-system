package com.flexiwork.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RatingRequest {

    @NotNull(message = "applicationId is required")
    private Long applicationId;

    @NotBlank(message = "raterType is required")
    private String raterType;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int value;

    private String comment = "";

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }

    public String getRaterType() { return raterType; }
    public void setRaterType(String raterType) { this.raterType = raterType; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment != null ? comment : ""; }
}
