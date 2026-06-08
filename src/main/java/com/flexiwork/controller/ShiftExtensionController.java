package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.dto.ShiftExtensionRequest;
import com.flexiwork.enums.ApplicationStatus;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.*;
import com.flexiwork.repository.*;
import com.flexiwork.service.AuditLogService;
import com.flexiwork.service.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs/{jobId}/extensions")
@PreAuthorize("hasRole('EMPLOYER')")
public class ShiftExtensionController {

    private final ShiftExtensionRepository extensionRepo;
    private final ApplicationRepository applicationRepo;
    private final JobRepository jobRepo;
    private final QRVerificationRepository qrRepo;
    private final SmsService smsService;
    private final AuditLogService auditLogService;

    public ShiftExtensionController(ShiftExtensionRepository extensionRepo,
                                    ApplicationRepository applicationRepo,
                                    JobRepository jobRepo,
                                    QRVerificationRepository qrRepo,
                                    SmsService smsService,
                                    AuditLogService auditLogService) {
        this.extensionRepo = extensionRepo;
        this.applicationRepo = applicationRepo;
        this.jobRepo = jobRepo;
        this.qrRepo = qrRepo;
        this.smsService = smsService;
        this.auditLogService = auditLogService;
    }

    /** List extensions for a job */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftExtension>>> list(@PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.success("Extensions", extensionRepo.findByJobJobId(jobId)));
    }

    /**
     * Create a shift extension.
     * Body: {
     *   newEndTime: "21:00",
     *   extensionWage: 2500,
     *   notes: "Night shift",
     *   applicationIds: [1, 2, 3]
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ShiftExtension>> create(
            @PathVariable Long jobId,
            @Valid @RequestBody ShiftExtensionRequest body,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {

        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        // Verify caller owns this job
        if (!job.getCompany().getCompanyId().equals(principal.getCompanyId())) {
            throw new BusinessException("Not authorized to extend this job's shift");
        }

        // Job must be today
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Colombo"));
        if (job.getShiftDate() == null || !job.getShiftDate().equals(today)) {
            throw new BusinessException("Shift extensions can only be created on the shift date");
        }

        LocalTime newEndTime;
        try {
            newEndTime = LocalTime.parse(body.getNewEndTime());
        } catch (Exception e) {
            throw new BusinessException("Invalid newEndTime format, expected HH:mm");
        }
        BigDecimal extWage = body.getExtensionWage();
        String notes       = body.getNotes();
        List<Long> appIds  = body.getApplicationIds();

        ShiftExtension extension = new ShiftExtension();
        extension.setJob(job);
        extension.setNewEndTime(newEndTime);
        extension.setExtensionWage(extWage);
        extension.setNotes(notes);
        extension.setCreatedBy(principal.getCompanyId());
        ShiftExtension saved = extensionRepo.save(extension);

        List<ShiftExtensionWorker> extWorkers = new ArrayList<>();
        for (Long appId : appIds) {
            Application app = applicationRepo.findById(appId)
                    .orElseThrow(() -> new ResourceNotFoundException("Application", appId));

            if (app.getStatus() != ApplicationStatus.APPROVED) continue;

            // Verify worker has checked in
            boolean checkedIn = qrRepo.findByUserUserIdAndJobJobId(
                app.getUser().getUserId(), jobId)
                .map(q -> q.getCheckInTime() != null)
                .orElse(false);
            if (!checkedIn) continue;

            ShiftExtensionWorker ew = new ShiftExtensionWorker();
            ew.setExtension(saved);
            ew.setApplication(app);
            ew.setNotifiedAt(LocalDateTime.now(ZoneId.of("Asia/Colombo")));
            extWorkers.add(ew);

            // Notify worker via WhatsApp
            sendExtensionNotification(app.getUser(), job, newEndTime, extWage);
        }
        saved.setWorkers(extWorkers);
        extensionRepo.save(saved);

        auditLogService.log(principal.getId(), "EMPLOYER", "SHIFT_EXTENDED",
            "Job", jobId,
            "Extended shift for " + extWorkers.size() + " workers until " + newEndTime,
            req.getRemoteAddr());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                    "Shift extended for " + extWorkers.size() + " workers. They've been notified via WhatsApp.",
                    saved));
    }

    private void sendExtensionNotification(User worker, Job job, LocalTime newEnd, BigDecimal wage) {
        String message =
            "⏰ *FlexiWork — Shift Extended!*\n\n" +
            "Hello *" + worker.getFullName() + "*,\n\n" +
            "Your shift has been *extended* by the manager.\n\n" +
            "📌 Job: " + job.getTitle() + "\n" +
            "🏭 Location: " + (job.getFactoryLocation() != null ? job.getFactoryLocation() : job.getDistrict()) + "\n" +
            "🕐 New End Time: *" + newEnd + "*\n" +
            "💵 Extension Wage: *LKR " + String.format("%,.2f", wage) + "*\n\n" +
            "Please continue your work until the new end time. Thank you! 🙏";

        try {
            smsService.sendCustomMessage(worker, message);
        } catch (Exception e) {
            // Log but don't fail the extension creation
        }
    }
}
