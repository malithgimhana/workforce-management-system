package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "shift_extensions")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ShiftExtension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long extensionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Job job;

    @Column(nullable = false)
    private LocalTime newEndTime;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal extensionWage;

    @Column(length = 200)
    private String notes;

    @Column(name = "created_by")
    private Long createdBy; // companyId

    @OneToMany(mappedBy = "extension", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"extension"})
    private List<ShiftExtensionWorker> workers;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public ShiftExtension() {}

    public Long getExtensionId() { return extensionId; }
    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }
    public LocalTime getNewEndTime() { return newEndTime; }
    public void setNewEndTime(LocalTime newEndTime) { this.newEndTime = newEndTime; }
    public BigDecimal getExtensionWage() { return extensionWage; }
    public void setExtensionWage(BigDecimal extensionWage) { this.extensionWage = extensionWage; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }
    public List<ShiftExtensionWorker> getWorkers() { return workers; }
    public void setWorkers(List<ShiftExtensionWorker> workers) { this.workers = workers; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
