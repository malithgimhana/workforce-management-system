package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexiwork.enums.RaterType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ratings",
    uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "rater_type"}))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ratingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(name = "rater_type", nullable = false, length = 10)
    private RaterType raterType;

    @Column(nullable = false)
    private Integer ratingValue; // 1–5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Rating() {}

    // ── Getters / Setters ──────────────────────────────────────────────
    public Long getRatingId() { return ratingId; }
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
    public RaterType getRaterType() { return raterType; }
    public void setRaterType(RaterType raterType) { this.raterType = raterType; }
    public Integer getRatingValue() { return ratingValue; }
    public void setRatingValue(Integer ratingValue) { this.ratingValue = ratingValue; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
