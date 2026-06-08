package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.dto.RatingRequest;
import com.flexiwork.enums.ApplicationStatus;
import com.flexiwork.enums.RaterType;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.Application;
import com.flexiwork.model.Rating;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.ApplicationRepository;
import com.flexiwork.repository.QRVerificationRepository;
import com.flexiwork.repository.RatingRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingRepository ratingRepository;
    private final ApplicationRepository applicationRepository;
    private final QRVerificationRepository qrVerificationRepository;

    public RatingController(RatingRepository ratingRepository,
                            ApplicationRepository applicationRepository,
                            QRVerificationRepository qrVerificationRepository) {
        this.ratingRepository = ratingRepository;
        this.applicationRepository = applicationRepository;
        this.qrVerificationRepository = qrVerificationRepository;
    }

    /**
     * Submit a rating.
     * Body: { applicationId, raterType: "WORKER"|"COMPANY", value: 1-5, comment: "..." }
     */
    @PostMapping
    @PreAuthorize("hasRole('WORKER') or hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Rating>> submit(
            @Valid @RequestBody RatingRequest body,
            @AuthenticationPrincipal UserPrincipal principal) {

        Long applicationId = body.getApplicationId();
        RaterType raterType;
        try {
            raterType = RaterType.valueOf(body.getRaterType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid raterType: must be WORKER or COMPANY");
        }
        int value = body.getValue();
        String comment = body.getComment();

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            throw new BusinessException("Can only rate completed (approved) applications");
        }

        // Verify checkout happened
        boolean checkedOut = qrVerificationRepository
                .findByUserUserIdAndJobJobId(app.getUser().getUserId(), app.getJob().getJobId())
                .map(q -> q.getCheckOutTime() != null)
                .orElse(false);
        if (!checkedOut) {
            throw new BusinessException("Rating is only available after the worker has checked out");
        }

        // Verify the caller is the right party
        String role = principal.getRole();
        if (raterType == RaterType.WORKER && !"WORKER".equals(role)) {
            throw new BusinessException("Only workers can submit a WORKER rating");
        }
        if (raterType == RaterType.COMPANY && !"EMPLOYER".equals(role)) {
            throw new BusinessException("Only employers can submit a COMPANY rating");
        }
        if (raterType == RaterType.WORKER && !app.getUser().getUserId().equals(principal.getId())) {
            throw new BusinessException("You can only rate your own jobs");
        }
        if (raterType == RaterType.COMPANY && !app.getJob().getCompany().getCompanyId().equals(principal.getCompanyId())) {
            throw new BusinessException("You can only rate workers from your own jobs");
        }

        if (ratingRepository.findByApplicationApplicationIdAndRaterType(applicationId, raterType).isPresent()) {
            throw new BusinessException("You have already submitted a rating for this application");
        }

        Rating rating = new Rating();
        rating.setApplication(app);
        rating.setRaterType(raterType);
        rating.setRatingValue(value);
        rating.setComment(comment);
        ratingRepository.save(rating);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Rating submitted", rating));
    }

    /** Get ratings for a worker (visible to all) */
    @GetMapping("/worker/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> workerRatings(@PathVariable Long userId) {
        List<Rating> ratings = ratingRepository.findRatingsForWorker(userId);
        Double avg = ratingRepository.avgWorkerRating(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("ratings", ratings);
        data.put("average", avg != null ? Math.round(avg * 10.0) / 10.0 : null);
        data.put("count", ratings.size());
        return ResponseEntity.ok(ApiResponse.success("Worker ratings", data));
    }

    /** Get ratings for a company (visible to all) */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> companyRatings(@PathVariable Long companyId) {
        List<Rating> ratings = ratingRepository.findRatingsForCompany(companyId);
        Double avg = ratingRepository.avgCompanyRating(companyId);
        Map<String, Object> data = new HashMap<>();
        data.put("ratings", ratings);
        data.put("average", avg != null ? Math.round(avg * 10.0) / 10.0 : null);
        data.put("count", ratings.size());
        return ResponseEntity.ok(ApiResponse.success("Company ratings", data));
    }
}
