package com.flexiwork.repository;

import com.flexiwork.enums.RaterType;
import com.flexiwork.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByApplicationApplicationIdAndRaterType(Long applicationId, RaterType raterType);

    // Ratings received by a worker (raterType = COMPANY means company rated the worker)
    @Query("SELECT r FROM Rating r WHERE r.application.user.userId = :userId AND r.raterType = 'COMPANY'")
    List<Rating> findRatingsForWorker(@Param("userId") Long userId);

    // Ratings received by a company (raterType = WORKER means worker rated the company)
    @Query("SELECT r FROM Rating r WHERE r.application.job.company.companyId = :companyId AND r.raterType = 'WORKER'")
    List<Rating> findRatingsForCompany(@Param("companyId") Long companyId);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.application.user.userId = :userId AND r.raterType = 'COMPANY'")
    Double avgWorkerRating(@Param("userId") Long userId);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.application.job.company.companyId = :companyId AND r.raterType = 'WORKER'")
    Double avgCompanyRating(@Param("companyId") Long companyId);
}
