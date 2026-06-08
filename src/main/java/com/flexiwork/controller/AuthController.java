package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.dto.CompanyRegisterRequest;
import com.flexiwork.dto.LoginRequest;
import com.flexiwork.dto.RegisterRequest;
import com.flexiwork.dto.UpdateProfileRequest;
import com.flexiwork.model.Company;
import com.flexiwork.model.User;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.service.AuditLogService;
import com.flexiwork.service.CompanyService;
import com.flexiwork.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOGIN_WINDOW_MS = 60_000;
    private final Map<String, Deque<Long>> loginAttempts = new ConcurrentHashMap<>();

    private boolean isRateLimited(String ip) {
        long now = System.currentTimeMillis();
        Deque<Long> timestamps = loginAttempts.compute(ip, (k, q) -> {
            if (q == null) q = new ArrayDeque<>();
            while (!q.isEmpty() && now - q.peekFirst() > LOGIN_WINDOW_MS) q.pollFirst();
            q.addLast(now);
            return q;
        });
        return timestamps.size() > MAX_LOGIN_ATTEMPTS;
    }

    private final UserService userService;
    private final CompanyService companyService;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLogService;
    private final com.flexiwork.config.JwtUtil jwtUtil;

    public AuthController(UserService userService, CompanyService companyService,
                          AuthenticationManager authenticationManager,
                          AuditLogService auditLogService,
                          com.flexiwork.config.JwtUtil jwtUtil) {
        this.userService = userService;
        this.companyService = companyService;
        this.authenticationManager = authenticationManager;
        this.auditLogService = auditLogService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/register", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(
            @Valid @ModelAttribute RegisterRequest request,
            @RequestParam(value = "photo",    required = false) MultipartFile photo,
            @RequestParam(value = "nicFront", required = false) MultipartFile nicFront,
            @RequestParam(value = "nicBack",  required = false) MultipartFile nicBack) {
        User user = userService.registerWorker(request, photo, nicFront, nicBack);
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getUserId());
        data.put("firstName", user.getFirstName());
        data.put("lastName", user.getLastName());
        data.put("phone", user.getPhone());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Registration successful", data));
    }

    @PostMapping(value = "/company/register", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerCompany(
            @Valid @ModelAttribute CompanyRegisterRequest request,
            @RequestParam(value = "brCert", required = false) MultipartFile brCert) {
        Company company = companyService.registerCompany(request, brCert);
        Map<String, Object> data = new HashMap<>();
        data.put("companyId", company.getCompanyId());
        data.put("name", company.getName());
        data.put("email", company.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company registration successful", data));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        if (isRateLimited(httpRequest.getRemoteAddr())) {
            return ResponseEntity.status(429)
                    .body(ApiResponse.error("Too many login attempts. Please try again in a minute."));
        }
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getIdentifier(), request.getPassword())
            );

            UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
            String token = jwtUtil.generateToken(principal.getId(), principal.getRole(), principal.getUsername(), principal.getCompanyId());
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("id", principal.getId());
            data.put("name", principal.getFullName());
            data.put("firstName", principal.getFirstName());
            data.put("lastName", principal.getLastName());
            data.put("fullName", principal.getFullName());
            data.put("email", principal.getUsername());
            data.put("role", principal.getRole());
            if (principal.getCompanyId() != null) {
                data.put("companyId", principal.getCompanyId());
            }
            if (principal.getCompanyRole() != null) {
                data.put("companyRole", principal.getCompanyRole());
            }
            auditLogService.log(principal.getId(), principal.getRole(), "LOGIN",
                "Login from " + httpRequest.getRemoteAddr(), httpRequest.getRemoteAddr());
            return ResponseEntity.ok(ApiResponse.success("Login successful", data));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request,
                                                      @AuthenticationPrincipal UserPrincipal principal) {
        if (principal != null) {
            auditLogService.log(principal.getId(), principal.getRole(), "LOGOUT",
                "Logout", request.getRemoteAddr());
        }
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateMe(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        User updated = userService.updateProfile(principal.getId(), request);
        Map<String, Object> data = new HashMap<>();
        data.put("id",        updated.getUserId());
        data.put("firstName", updated.getFirstName());
        data.put("lastName",  updated.getLastName());
        data.put("fullName",  updated.getFullName());
        data.put("email",     updated.getEmail());
        data.put("phone",     updated.getPhone());
        data.put("address",   updated.getAddress());
        data.put("district",  updated.getDistrict());
        data.put("gender",    updated.getGender());
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", data));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Map<String, Object>>> me(
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated"));
        }
        // Fetch full user details for profile pre-fill
        User fullUser = null;
        try { fullUser = userService.findById(principal.getId()); } catch (Exception ignored) {}

        Map<String, Object> data = new HashMap<>();
        data.put("id",        principal.getId());
        data.put("firstName", principal.getFirstName());
        data.put("lastName",  principal.getLastName());
        data.put("fullName",  principal.getFullName());
        data.put("email",     principal.getUsername());
        data.put("role",      principal.getRole());
        if (principal.getCompanyId() != null) data.put("companyId", principal.getCompanyId());
        if (principal.getCompanyRole() != null) data.put("companyRole", principal.getCompanyRole());
        if (fullUser != null) {
            data.put("phone",    fullUser.getPhone());
            data.put("nic",      fullUser.getNic());
            data.put("address",  fullUser.getAddress());
            data.put("district", fullUser.getDistrict());
            data.put("gender",   fullUser.getGender());
            data.put("photo",    fullUser.getPhoto());
        }
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }
}
