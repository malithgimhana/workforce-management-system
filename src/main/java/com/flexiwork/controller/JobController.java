package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.dto.JobPostRequest;
import com.flexiwork.dto.PageResponse;
import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.enums.JobCategory;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.model.Company;
import com.flexiwork.model.Job;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.CompanyRepository;
import com.flexiwork.service.AuditLogService;
import com.flexiwork.service.JobService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final CompanyRepository companyRepository;
    private final AuditLogService auditLogService;

    public JobController(JobService jobService,
                         CompanyRepository companyRepository,
                         AuditLogService auditLogService) {
        this.jobService = jobService;
        this.companyRepository = companyRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Job>>> searchJobs(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Double minWage,
            @RequestParam(required = false) Double maxWage,
            @RequestParam(required = false) JobCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        PageResponse<Job> jobs = jobService.searchJobs(district, minWage, maxWage, category,
                                                        dateFrom, dateTo, keyword, page, size);
        return ResponseEntity.ok(ApiResponse.success("Jobs retrieved", jobs));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Job>> getJob(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success("Job retrieved", job));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Job>> postJob(
            @Valid @RequestBody JobPostRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        Long companyId = principal.getCompanyId();

        // ── Document verification check (skip for ADMIN) ──
        if (companyId != null) {
            Company company = companyRepository.findById(companyId).orElse(null);
            if (company != null && company.getDocStatus() != DocumentStatus.APPROVED) {
                throw new BusinessException("Your Business Registration Certificate must be verified before posting jobs. Please upload your BR certificate and wait for admin approval.");
            }
        }

        Job job = jobService.postJob(request, companyId);
        auditLogService.log(principal.getId(), principal.getRole(), "JOB_CREATED",
            "Job", job.getJobId(), "Job: " + job.getTitle(), req.getRemoteAddr());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job posted successfully", job));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Job>> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobPostRequest request,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        if (!"ADMIN".equals(principal.getRole())) {
            Job existing = jobService.getJobById(id);
            if (!existing.getCompany().getCompanyId().equals(principal.getCompanyId())) {
                throw new BusinessException("You do not own this job");
            }
        }
        Job job = jobService.updateJob(id, request);
        auditLogService.log(principal.getId(), principal.getRole(), "JOB_UPDATED",
            "Job", id, "Job updated: " + job.getTitle(), req.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Job updated", job));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        if (!"ADMIN".equals(principal.getRole())) {
            Job existing = jobService.getJobById(id);
            if (!existing.getCompany().getCompanyId().equals(principal.getCompanyId())) {
                throw new BusinessException("You do not own this job");
            }
        }
        jobService.softDeleteJob(id);
        auditLogService.log(principal.getId(), principal.getRole(), "JOB_DELETED",
            "Job", id, "Job soft-deleted", req.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.<Void>success("Job deleted"));
    }

    @GetMapping("/company")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Job>>> getCompanyJobs(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        List<Job> jobs = jobService.getJobsByCompany(companyId);
        return ResponseEntity.ok(ApiResponse.success("Company jobs retrieved", jobs));
    }
}
