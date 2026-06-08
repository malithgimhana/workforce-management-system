package com.flexiwork.service;

import com.flexiwork.model.AuditLog;
import com.flexiwork.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;

    public AuditLogService(AuditLogRepository repo) {
        this.repo = repo;
    }

    @Async
    public void log(Long userId, String userType, String action,
                    String entityType, Long entityId, String details, String ip) {
        AuditLog entry = new AuditLog();
        entry.setUserId(userId);
        entry.setUserType(userType);
        entry.setAction(action);
        entry.setEntityType(entityType);
        entry.setEntityId(entityId);
        entry.setDetails(details);
        entry.setIpAddress(ip);
        repo.save(entry);
    }

    /** Shortcut — no entity */
    @Async
    public void log(Long userId, String userType, String action, String details, String ip) {
        log(userId, userType, action, null, null, details, ip);
    }
}
