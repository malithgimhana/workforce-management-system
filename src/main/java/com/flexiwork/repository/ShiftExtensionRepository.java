package com.flexiwork.repository;

import com.flexiwork.model.ShiftExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftExtensionRepository extends JpaRepository<ShiftExtension, Long> {
    List<ShiftExtension> findByJobJobId(Long jobId);
}
