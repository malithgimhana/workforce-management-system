package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.Company;
import com.flexiwork.model.User;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.CompanyRepository;
import com.flexiwork.repository.UserRepository;
import com.flexiwork.service.AuditLogService;
import com.flexiwork.service.EmailService;
import com.flexiwork.service.FileUploadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/documents")
public class DocumentVerificationController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final FileUploadService fileUploadService;
    private final AuditLogService auditLogService;
    private final EmailService emailService;

    public DocumentVerificationController(UserRepository userRepository,
                                          CompanyRepository companyRepository,
                                          FileUploadService fileUploadService,
                                          AuditLogService auditLogService,
                                          EmailService emailService) {
        this.userRepository    = userRepository;
        this.companyRepository = companyRepository;
        this.fileUploadService = fileUploadService;
        this.auditLogService   = auditLogService;
        this.emailService      = emailService;
    }

    // ── Worker uploads NIC front + back ──────────────────────────────────────
    @PostMapping("/worker/upload")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadWorkerDocs(
            @RequestParam("nicFront") MultipartFile nicFront,
            @RequestParam("nicBack") MultipartFile nicBack,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));

        String frontPath = fileUploadService.uploadFile(nicFront, "docs/nic");
        String backPath  = fileUploadService.uploadFile(nicBack,  "docs/nic");

        user.setNicFrontPath(frontPath);
        user.setNicBackPath(backPath);
        user.setDocStatus(DocumentStatus.PENDING);
        user.setDocRejectReason(null);
        user.setDocSubmittedAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        auditLogService.log(principal.getId(), "WORKER", "DOC_SUBMITTED",
            "User", principal.getId(), "NIC documents submitted for verification", req.getRemoteAddr());

        emailService.sendWorkerDocSubmitted(user.getEmail(), user.getFirstName());
        emailService.sendAdminDocAlert(user.getFullName(), "Worker NIC");

        Map<String, String> result = new HashMap<>();
        result.put("nicFrontPath", frontPath);
        result.put("nicBackPath", backPath);
        result.put("docStatus", "PENDING");
        return ResponseEntity.ok(ApiResponse.success("Documents submitted for verification", result));
    }

    // ── Company uploads BR certificate ────────────────────────────────────────
    @PostMapping("/company/upload")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadCompanyDoc(
            @RequestParam("brCert") MultipartFile brCert,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {

        Company company = companyRepository.findById(principal.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", principal.getCompanyId()));

        String certPath = fileUploadService.uploadFile(brCert, "docs/br");
        company.setBrCertPath(certPath);
        company.setDocStatus(DocumentStatus.PENDING);
        company.setDocRejectReason(null);
        company.setDocSubmittedAt(java.time.LocalDateTime.now());
        companyRepository.save(company);

        auditLogService.log(principal.getId(), "EMPLOYER", "DOC_SUBMITTED",
            "Company", company.getCompanyId(), "BR certificate submitted for verification", req.getRemoteAddr());

        emailService.sendCompanyDocSubmitted(company.getEmail(), company.getName());
        emailService.sendAdminDocAlert(company.getName(), "Company BR Certificate");

        Map<String, String> result = new HashMap<>();
        result.put("brCertPath", certPath);
        result.put("docStatus", "PENDING");
        return ResponseEntity.ok(ApiResponse.success("BR certificate submitted for verification", result));
    }

    // ── Admin — view pending queue ─────────────────────────────────────────
    @GetMapping("/admin/queue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPendingQueue() {
        List<User> pendingWorkers = userRepository.findByDocStatus(DocumentStatus.PENDING);
        List<Company> pendingCompanies = companyRepository.findByDocStatus(DocumentStatus.PENDING);

        Map<String, Object> data = new HashMap<>();
        data.put("workers", pendingWorkers.stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("userId", u.getUserId());
            m.put("name", u.getFullName());
            m.put("nic", u.getNic());
            m.put("phone", u.getPhone());
            m.put("nicFrontPath", u.getNicFrontPath());
            m.put("nicBackPath", u.getNicBackPath());
            m.put("docStatus", u.getDocStatus());
            return m;
        }).toList());
        data.put("companies", pendingCompanies.stream().map(c -> {
            Map<String, Object> m = new HashMap<>();
            m.put("companyId", c.getCompanyId());
            m.put("name", c.getName());
            m.put("brNumber", c.getBrNumber());
            m.put("phone", c.getPhone());
            m.put("brCertPath", c.getBrCertPath());
            m.put("docStatus", c.getDocStatus());
            return m;
        }).toList());
        return ResponseEntity.ok(ApiResponse.success("Verification queue", data));
    }

    // ── Admin — approve worker docs ────────────────────────────────────────
    @PostMapping("/admin/worker/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> approveWorker(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserPrincipal admin,
            HttpServletRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setDocStatus(DocumentStatus.APPROVED);
        user.setDocRejectReason(null);
        userRepository.save(user);
        auditLogService.log(admin.getId(), "ADMIN", "DOC_APPROVED", "User", userId,
            "Worker NIC documents approved", req.getRemoteAddr());
        emailService.sendWorkerDocApproved(user.getEmail(), user.getFirstName());
        return ResponseEntity.ok(ApiResponse.success("Worker documents approved"));
    }

    // ── Admin — reject worker docs ─────────────────────────────────────────
    @PostMapping("/admin/worker/{userId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectWorker(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal admin,
            HttpServletRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setDocStatus(DocumentStatus.REJECTED);
        user.setDocRejectReason(body.getOrDefault("reason", "Documents not accepted"));
        userRepository.save(user);
        auditLogService.log(admin.getId(), "ADMIN", "DOC_REJECTED", "User", userId,
            "Reason: " + user.getDocRejectReason(), req.getRemoteAddr());
        emailService.sendWorkerDocRejected(user.getEmail(), user.getFirstName(), user.getDocRejectReason());
        return ResponseEntity.ok(ApiResponse.success("Worker documents rejected"));
    }

    // ── Admin — approve company docs ───────────────────────────────────────
    @PostMapping("/admin/company/{companyId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> approveCompany(
            @PathVariable Long companyId,
            @AuthenticationPrincipal UserPrincipal admin,
            HttpServletRequest req) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
        company.setDocStatus(DocumentStatus.APPROVED);
        company.setDocRejectReason(null);
        companyRepository.save(company);
        auditLogService.log(admin.getId(), "ADMIN", "DOC_APPROVED", "Company", companyId,
            "Company BR certificate approved", req.getRemoteAddr());
        emailService.sendCompanyDocApproved(company.getEmail(), company.getName());
        return ResponseEntity.ok(ApiResponse.success("Company documents approved"));
    }

    // ── Admin — reject company docs ────────────────────────────────────────
    @PostMapping("/admin/company/{companyId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectCompany(
            @PathVariable Long companyId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal admin,
            HttpServletRequest req) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
        company.setDocStatus(DocumentStatus.REJECTED);
        company.setDocRejectReason(body.getOrDefault("reason", "Documents not accepted"));
        companyRepository.save(company);
        auditLogService.log(admin.getId(), "ADMIN", "DOC_REJECTED", "Company", companyId,
            "Reason: " + company.getDocRejectReason(), req.getRemoteAddr());
        emailService.sendCompanyDocRejected(company.getEmail(), company.getName(), company.getDocRejectReason());
        return ResponseEntity.ok(ApiResponse.success("Company documents rejected"));
    }

    // ── Worker — get own doc status ────────────────────────────────────────
    @GetMapping("/my-status")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> myDocStatus(
            @AuthenticationPrincipal UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", principal.getId()));
        Map<String, Object> data = new HashMap<>();
        data.put("docStatus", user.getDocStatus());
        data.put("docRejectReason", user.getDocRejectReason());
        data.put("nicFrontPath", user.getNicFrontPath());
        data.put("nicBackPath", user.getNicBackPath());
        return ResponseEntity.ok(ApiResponse.success("Document status", data));
    }

    // ── Employer — get own doc status ─────────────────────────────────────
    @GetMapping("/company-status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> companyDocStatus(
            @AuthenticationPrincipal UserPrincipal principal) {
        Company company = companyRepository.findById(principal.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", principal.getCompanyId()));
        Map<String, Object> data = new HashMap<>();
        data.put("docStatus", company.getDocStatus());
        data.put("docRejectReason", company.getDocRejectReason());
        data.put("brCertPath", company.getBrCertPath());
        return ResponseEntity.ok(ApiResponse.success("Document status", data));
    }
}
