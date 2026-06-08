package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.model.CompanyUser;
import com.flexiwork.model.SecurityGuard;
import com.flexiwork.model.User;
import com.flexiwork.repository.CompanyUserRepository;
import com.flexiwork.repository.SecurityGuardRepository;
import com.flexiwork.repository.UserRepository;
import com.flexiwork.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final UserRepository userRepository;
    private final CompanyUserRepository companyUserRepository;
    private final SecurityGuardRepository securityGuardRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // OTP store: expires after 10 minutes, max 1000 entries
    private final Cache<String, String> otpStore = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    public PasswordResetController(UserRepository userRepository,
                                   CompanyUserRepository companyUserRepository,
                                   SecurityGuardRepository securityGuardRepository,
                                   PasswordEncoder passwordEncoder,
                                   EmailService emailService) {
        this.userRepository          = userRepository;
        this.companyUserRepository   = companyUserRepository;
        this.securityGuardRepository = securityGuardRepository;
        this.passwordEncoder         = passwordEncoder;
        this.emailService            = emailService;
    }

    // Step 1 — send OTP to email
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isBlank())
            return ResponseEntity.badRequest().body(ApiResponse.error("Email is required"));

        email = email.toLowerCase().trim();

        // Check all three user tables
        boolean found = userRepository.findByEmail(email).isPresent()
                     || companyUserRepository.findByEmail(email).isPresent()
                     || securityGuardRepository.findByEmail(email).isPresent();

        if (!found)
            return ResponseEntity.badRequest().body(ApiResponse.error("No account found with this email address"));

        String otp = String.valueOf(100000 + (int)(Math.random() * 900000));
        otpStore.put(email, otp);

        emailService.sendPasswordResetOtp(email, otp);

        return ResponseEntity.ok(ApiResponse.success("OTP sent to your email address"));
    }

    // Step 2 — verify OTP and reset password across all user types
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody Map<String, String> body) {
        String email   = body.get("email");
        String otp     = body.get("otp");
        String newPass = body.get("newPassword");

        if (email == null || otp == null || newPass == null)
            return ResponseEntity.badRequest().body(ApiResponse.error("Email, OTP and new password are required"));

        email = email.toLowerCase().trim();

        if (newPass.length() < 8)
            return ResponseEntity.badRequest().body(ApiResponse.error("Password must be at least 8 characters"));

        String storedOtp = otpStore.getIfPresent(email);
        if (storedOtp == null || !storedOtp.equals(otp))
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid or expired OTP"));

        boolean updated = false;

        // Worker
        User worker = userRepository.findByEmail(email).orElse(null);
        if (worker != null) {
            worker.setPassword(passwordEncoder.encode(newPass));
            userRepository.save(worker);
            updated = true;
        }

        // Employer / IT Admin
        if (!updated) {
            CompanyUser cu = companyUserRepository.findByEmail(email).orElse(null);
            if (cu != null) {
                cu.setPassword(passwordEncoder.encode(newPass));
                companyUserRepository.save(cu);
                updated = true;
            }
        }

        // Security Guard
        if (!updated) {
            SecurityGuard guard = securityGuardRepository.findByEmail(email).orElse(null);
            if (guard != null) {
                guard.setPassword(passwordEncoder.encode(newPass));
                securityGuardRepository.save(guard);
                updated = true;
            }
        }

        if (!updated)
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));

        otpStore.invalidate(email);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully. Please login with your new password."));
    }

}
