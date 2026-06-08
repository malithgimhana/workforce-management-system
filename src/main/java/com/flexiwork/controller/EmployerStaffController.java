package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.enums.CompanyRole;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.*;
import com.flexiwork.repository.*;
import com.flexiwork.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Allows HR Manager (main company account or company user with HR_MANAGER role)
 * to manage their own staff: IT Admins and Security Guards.
 */
@RestController
@RequestMapping("/api/employer/staff")
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerStaffController {

    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final SecurityGuardRepository securityGuardRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public EmployerStaffController(CompanyRepository companyRepository,
                                   CompanyUserRepository companyUserRepository,
                                   SecurityGuardRepository securityGuardRepository,
                                   PasswordEncoder passwordEncoder,
                                   AuditLogService auditLogService) {
        this.companyRepository = companyRepository;
        this.companyUserRepository = companyUserRepository;
        this.securityGuardRepository = securityGuardRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    // ── IT Admins ────────────────────────────────────────────────────────────

    @GetMapping("/it-admins")
    public ResponseEntity<ApiResponse<List<CompanyUser>>> listItAdmins(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
        List<CompanyUser> admins = companyUserRepository.findByCompanyAndRole(company, CompanyRole.IT_ADMIN);
        return ResponseEntity.ok(ApiResponse.success("IT Admins", admins));
    }

    @PostMapping("/it-admins")
    public ResponseEntity<ApiResponse<CompanyUser>> createItAdmin(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        // Only HR_MANAGER or main company account (no companyRole) can add staff
        assertHrAccess(principal);

        Long companyId = principal.getCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));

        String email = body.get("email");
        if (companyUserRepository.findByEmail(email).isPresent())
            throw new BusinessException("Email already in use");

        CompanyUser cu = new CompanyUser();
        cu.setName(body.get("name"));
        cu.setEmail(email);
        cu.setPassword(passwordEncoder.encode(body.get("password")));
        cu.setRole(CompanyRole.IT_ADMIN);
        cu.setCompany(company);
        companyUserRepository.save(cu);

        auditLogService.log(principal.getId(), "EMPLOYER", "STAFF_CREATED",
            "CompanyUser", cu.getCompanyUserId(),
            "IT Admin " + cu.getName() + " created", req.getRemoteAddr());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("IT Admin created", cu));
    }

    @DeleteMapping("/it-admins/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItAdmin(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        assertHrAccess(principal);
        CompanyUser cu = companyUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CompanyUser", id));
        if (!cu.getCompany().getCompanyId().equals(principal.getCompanyId()))
            throw new BusinessException("Access denied");
        companyUserRepository.delete(cu);
        auditLogService.log(principal.getId(), "EMPLOYER", "STAFF_REMOVED",
            "CompanyUser", id, "IT Admin removed", req.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("IT Admin removed"));
    }

    // ── Security Guards ───────────────────────────────────────────────────────

    @GetMapping("/guards")
    public ResponseEntity<ApiResponse<List<SecurityGuard>>> listGuards(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));
        List<SecurityGuard> guards = securityGuardRepository.findByCompany(company);
        return ResponseEntity.ok(ApiResponse.success("Guards", guards));
    }

    @PostMapping("/guards")
    public ResponseEntity<ApiResponse<SecurityGuard>> createGuard(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        assertHrAccess(principal);

        Long companyId = principal.getCompanyId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));

        String email = body.get("email");
        if (securityGuardRepository.findByEmail(email).isPresent())
            throw new BusinessException("Email already in use by another guard");

        SecurityGuard guard = new SecurityGuard();
        guard.setName(body.get("name"));
        guard.setEmail(email);
        guard.setPhone(body.getOrDefault("phone", ""));
        guard.setPassword(passwordEncoder.encode(body.get("password")));
        guard.setCompany(company);
        guard.setIsActive(true);
        securityGuardRepository.save(guard);

        auditLogService.log(principal.getId(), "EMPLOYER", "GUARD_CREATED",
            "SecurityGuard", guard.getGuardId(),
            "Guard " + guard.getName() + " created", req.getRemoteAddr());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Security guard created", guard));
    }

    @DeleteMapping("/guards/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateGuard(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        assertHrAccess(principal);
        SecurityGuard guard = securityGuardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SecurityGuard", id));
        if (!guard.getCompany().getCompanyId().equals(principal.getCompanyId()))
            throw new BusinessException("Access denied");
        guard.setIsActive(false);
        securityGuardRepository.save(guard);
        auditLogService.log(principal.getId(), "EMPLOYER", "GUARD_DEACTIVATED",
            "SecurityGuard", id, "Guard deactivated", req.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Guard deactivated"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void assertHrAccess(UserPrincipal principal) {
        String cr = principal.getCompanyRole();
        // null means main company account (full access), HR_MANAGER also allowed
        if (cr != null && !cr.equals("HR_MANAGER") && !cr.equals("GM")) {
            throw new BusinessException("Only HR Manager can manage staff");
        }
    }
}
