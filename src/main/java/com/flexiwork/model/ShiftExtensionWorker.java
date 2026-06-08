package com.flexiwork.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift_extension_workers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ShiftExtensionWorker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extension_id", nullable = false)
    @JsonIgnoreProperties({"workers", "hibernateLazyInitializer", "handler"})
    private ShiftExtension extension;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "application_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Application application;

    private LocalDateTime notifiedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public ShiftExtensionWorker() {}

    public Long getId() { return id; }
    public ShiftExtension getExtension() { return extension; }
    public void setExtension(ShiftExtension extension) { this.extension = extension; }
    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }
    public LocalDateTime getNotifiedAt() { return notifiedAt; }
    public void setNotifiedAt(LocalDateTime notifiedAt) { this.notifiedAt = notifiedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
