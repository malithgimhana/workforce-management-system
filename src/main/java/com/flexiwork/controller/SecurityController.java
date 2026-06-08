package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.Company;
import com.flexiwork.model.SecurityGuard;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.CompanyRepository;
import com.flexiwork.repository.SecurityGuardRepository;
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

@RestController
@RequestMapping("/api/admin/security-guards")
@PreAuthorize("hasRole('ADMIN')")
public class SecurityController {

    private final SecurityGuardRepository guardRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public SecurityController(SecurityGuardRepository guardRepository,
                              CompanyRepository companyRepository,
                              PasswordEncoder passwordEncoder,
                              AuditLogService auditLogService) {
        this.guardRepository = guardRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SecurityGuard>>> listAll() {
        return ResponseEntity.ok(ApiResponse.success("Guards", guardRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SecurityGuard>> create(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal admin,
            HttpServletRequest req) {

        Long companyId = Long.valueOf(body.get("companyId").toString());
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));

        String email = body.get("email").toString();
        if (guardRepository.findByEmail(email).isPresent())
            throw new BusinessException("Email already in use by another guard");

        SecurityGuard guard = new SecurityGuard();
        guard.setName(body.get("name").toString());
        guard.setEmail(email);
        guard.setPhone(body.getOrDefault("phone", "").toString());
        guard.setPassword(passwordEncoder.encode(body.get("password").toString()));
        guard.setCompany(company);
        guard.setIsActive(true);
        guardRepository.save(guard);

        auditLogService.log(admin.getId(), "ADMIN", "GUARD_CREATED",
            "SecurityGuard", guard.getGuardId(),
            "Guard " + guard.getName() + " created for company " + company.getName(),
            req.getRemoteAddr());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Security guard created", guard));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal admin,
            HttpServletRequest req) {
        SecurityGuard guard = guardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SecurityGuard", id));
        guard.setIsActive(false);
        guardRepository.save(guard);
        auditLogService.log(admin.getId(), "ADMIN", "GUARD_DEACTIVATED",
            "SecurityGuard", id, "Guard deactivated", req.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Guard deactivated"));
    }
}
