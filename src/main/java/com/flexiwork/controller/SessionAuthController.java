package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.dto.LoginRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/session")
public class SessionAuthController {

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> sessionLogin(
            @Valid @RequestBody LoginRequest request, HttpSession session) {
        // Session-based auth - store identifier in session
        session.setAttribute("user", request.getIdentifier());
        return ResponseEntity.ok(ApiResponse.success("Session login successful", request.getIdentifier()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> sessionLogout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.<Void>success("Session logged out"));
    }
}
