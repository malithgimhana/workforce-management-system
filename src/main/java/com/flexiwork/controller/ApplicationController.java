package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.enums.ApplicationStatus;
import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.Application;
import com.flexiwork.model.Job;
import com.flexiwork.model.User;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.ApplicationRepository;
import com.flexiwork.repository.JobRepository;
import com.flexiwork.service.AuditLogService;
import com.flexiwork.service.QRService;
import com.flexiwork.service.SmsService;
import com.flexiwork.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserService userService;
    private final SmsService smsService;
    private final QRService qrService;
    private final AuditLogService auditLogService;

    public ApplicationController(ApplicationRepository applicationRepository,
                                  JobRepository jobRepository,
                                  UserService userService,
                                  SmsService smsService,
                                  QRService qrService,
                                  AuditLogService auditLogService) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userService = userService;
        this.smsService = smsService;
        this.qrService = qrService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    @PostMapping("/{jobId}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Application>> apply(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        User user = userService.findById(userId);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        // ── Document verification check ──
        if (user.getDocStatus() != DocumentStatus.APPROVED) {
            throw new BusinessException("Your NIC documents must be verified before applying for jobs. Please upload your NIC and wait for admin approval.");
        }

        if (Boolean.FALSE.equals(job.getIsActive()) || Boolean.TRUE.equals(job.getIsDeleted())) {
            throw new BusinessException("Job is no longer available");
        }

        if (job.getApprovedWorkers() >= job.getRequiredWorkers()) {
            throw new BusinessException("This job has already reached its worker quota");
        }

        applicationRepository.findByUserUserIdAndJobJobId(userId, jobId).ifPresent(a -> {
            throw new BusinessException("You have already applied for this job");
        });

        // ── Gender check ──
        if (job.getGender() != null
                && job.getGender() != com.flexiwork.enums.Gender.ANY
                && user.getGender() != null
                && user.getGender() != job.getGender()) {
            throw new BusinessException("This job is open to " + job.getGender().name().toLowerCase() + " workers only");
        }

        // ── Age check (derived from Sri Lanka NIC) ──
        int workerAge = extractAgeFromNic(user.getNic());
        if (workerAge > 0) {
            if (job.getMinAge() != null && workerAge < job.getMinAge()) {
                throw new BusinessException("Minimum age for this job is " + job.getMinAge() + " years");
            }
            if (job.getMaxAge() != null && workerAge > job.getMaxAge()) {
                throw new BusinessException("Maximum age for this job is " + job.getMaxAge() + " years");
            }
        }

        Application application = Application.builder()
                .user(user)
                .job(job)
                .status(ApplicationStatus.APPROVED)
                .build();
        applicationRepository.save(application);

        job.setApprovedWorkers(job.getApprovedWorkers() + 1);
        if (job.getApprovedWorkers() >= job.getRequiredWorkers()) {
            job.setIsActive(false);
        }
        jobRepository.save(job);

        try {
            qrService.generateQR(userId, jobId);
        } catch (Exception e) {
            log.error("QR generation failed for user {} job {}: {}", userId, jobId, e.getMessage());
        }

        smsService.sendApprovalNotification(user, job);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Applied! Your QR code is ready.", application));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Void>> withdraw(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long userId = principal.getId();
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));

        if (!application.getUser().getUserId().equals(userId)) {
            throw new BusinessException("Not authorized to withdraw this application");
        }

        if (application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new BusinessException("Application already withdrawn");
        }

        java.time.LocalDateTime shiftStart = application.getJob().getShiftDate()
                .atTime(application.getJob().getShiftStartTime());
        if (java.time.LocalDateTime.now().isAfter(shiftStart)) {
            throw new BusinessException("Cannot withdraw after shift has started");
        }

        Job withdrawJob = application.getJob();
        if (withdrawJob.getApprovedWorkers() > 0) {
            withdrawJob.setApprovedWorkers(withdrawJob.getApprovedWorkers() - 1);
            withdrawJob.setIsActive(true);
            jobRepository.save(withdrawJob);
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
        return ResponseEntity.ok(ApiResponse.<Void>success("Application withdrawn successfully"));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<List<Application>>> getJobApplicants(@PathVariable Long jobId) {
        List<Application> applications = applicationRepository.findByJobJobId(jobId);
        return ResponseEntity.ok(ApiResponse.success("Applicants retrieved", applications));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<List<Application>>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<Application> applications = applicationRepository.findByUserUserId(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Applications retrieved", applications));
    }

    /**
     * Cancel an APPROVED application (worker).
     * Allowed anytime EXCEPT within 1 hour of shift start.
     * Late cancellation is flagged on the application record.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        Long userId = principal.getId();
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));

        if (!application.getUser().getUserId().equals(userId)) {
            throw new BusinessException("Not authorized to cancel this application");
        }
        if (application.getStatus() == ApplicationStatus.CANCELLED ||
            application.getStatus() == ApplicationStatus.WITHDRAWN) {
            throw new BusinessException("Application already cancelled/withdrawn");
        }
        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new BusinessException("Only approved applications can be cancelled");
        }

        java.time.LocalDateTime shiftStart = application.getJob().getShiftDate()
                .atTime(application.getJob().getShiftStartTime());
        java.time.LocalDateTime now = java.time.LocalDateTime.now(
                java.time.ZoneId.of("Asia/Colombo"));

        boolean late = now.isAfter(shiftStart.minusHours(1));

        Job cancelJob = application.getJob();
        if (cancelJob.getApprovedWorkers() > 0) {
            cancelJob.setApprovedWorkers(cancelJob.getApprovedWorkers() - 1);
            cancelJob.setIsActive(true);
            jobRepository.save(cancelJob);
        }

        application.setStatus(ApplicationStatus.CANCELLED);
        application.setCancelledAt(now);
        application.setLateCancellation(late);
        applicationRepository.save(application);

        auditLogService.log(userId, "WORKER", "APP_CANCELLED", "Application", id,
            late ? "Late cancellation (within 1 hour of shift)" : "Cancelled before 1-hour window",
            req.getRemoteAddr());

        String msg = late
            ? "Application cancelled. Note: This is a late cancellation."
            : "Application cancelled successfully.";
        return ResponseEntity.ok(ApiResponse.success(msg));
    }

    /**
     * Extract age from Sri Lanka NIC.
     * Old format: 9 digits + V/X  → first 2 digits = birth year (1900+)
     * New format: 12 digits        → first 4 digits = birth year
     * Returns 0 if NIC cannot be parsed.
     */
    private int extractAgeFromNic(String nic) {
        if (nic == null || nic.isBlank()) return 0;
        try {
            nic = nic.trim().toUpperCase();
            int birthYear;
            int dayOfYear;
            if (nic.length() == 10 && (nic.endsWith("V") || nic.endsWith("X"))) {
                birthYear = 1900 + Integer.parseInt(nic.substring(0, 2));
                dayOfYear = Integer.parseInt(nic.substring(2, 5));
            } else if (nic.length() == 12) {
                birthYear = Integer.parseInt(nic.substring(0, 4));
                dayOfYear = Integer.parseInt(nic.substring(4, 7));
            } else {
                return 0;
            }
            // Female NIC adds 500 to day of year
            if (dayOfYear > 500) dayOfYear -= 500;
            java.time.LocalDate dob = java.time.LocalDate.ofYearDay(birthYear, dayOfYear);
            return java.time.Period.between(dob, java.time.LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }
}
