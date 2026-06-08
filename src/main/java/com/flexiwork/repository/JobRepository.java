package com.flexiwork.repository;

import com.flexiwork.enums.JobCategory;
import com.flexiwork.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j WHERE j.isDeleted = false AND j.isActive = true " +
           "AND (:category IS NULL OR j.category = :category) " +
           "AND (:minWage IS NULL OR j.dailyWage >= :minWage) " +
           "AND (:maxWage IS NULL OR j.dailyWage <= :maxWage) " +
           "AND (:district IS NULL OR LOWER(j.district) = LOWER(:district)) " +
           "AND (:dateFrom IS NULL OR j.shiftDate >= :dateFrom) " +
           "AND (:dateTo IS NULL OR j.shiftDate <= :dateTo) " +
           "AND (:keyword IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "     OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY j.shiftDate ASC, j.createdAt DESC")
    Page<Job> searchJobs(
        @Param("district") String district,
        @Param("minWage") Double minWage,
        @Param("maxWage") Double maxWage,
        @Param("category") JobCategory category,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    List<Job> findByCompanyCompanyIdAndIsDeletedFalse(Long companyId);

    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.shiftDate = :today AND j.shiftStartTime <= :cutoffTime")
    List<Job> findActiveJobsBeforeTime(@Param("today") LocalDate today, @Param("cutoffTime") LocalTime cutoffTime);

    @Query("SELECT j FROM Job j WHERE j.isActive = true AND j.approvedWorkers >= j.requiredWorkers")
    List<Job> findFullyBookedJobs();
}
