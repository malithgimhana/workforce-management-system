package com.flexiwork.repository;

import com.flexiwork.model.PendingNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingNotificationRepository extends JpaRepository<PendingNotification, Long> {
    List<PendingNotification> findByStatus(String status);
}
