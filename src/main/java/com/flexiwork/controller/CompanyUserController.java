package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.dto.CompanyUserRequest;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.Company;
import com.flexiwork.model.CompanyUser;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.CompanyUserRepository;
import com.flexiwork.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company/users")
public class CompanyUserController {

    private final CompanyUserRepository companyUserRepository;
    private final CompanyService companyService;
    private final PasswordEncoder passwordEncoder;

    public CompanyUserController(CompanyUserRepository companyUserRepository,
                                 CompanyService companyService,
                                 PasswordEncoder passwordEncoder) {
        this.companyUserRepository = companyUserRepository;
        this.companyService = companyService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CompanyUser>> createUser(
            @Valid @RequestBody CompanyUserRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        Company company = companyService.findById(companyId);

        if (companyUserRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already in use");
        }

        CompanyUser user = CompanyUser.builder()
                .company(company)
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company user created", companyUserRepository.save(user)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CompanyUser>>> getUsers(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        List<CompanyUser> users = companyUserRepository.findByCompanyCompanyId(companyId);
        return ResponseEntity.ok(ApiResponse.success("Company users retrieved", users));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {
        CompanyUser user = companyUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company user", id));
        if (!"ADMIN".equals(principal.getRole()) &&
                !user.getCompany().getCompanyId().equals(principal.getCompanyId())) {
            throw new BusinessException("Access denied");
        }
        companyUserRepository.delete(user);
        return ResponseEntity.ok(ApiResponse.<Void>success("Company user deleted"));
    }
}
