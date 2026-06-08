package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.dto.QRScanRequest;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.service.QRService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/qr")
public class QRController {

    private final QRService qrService;

    public QRController(QRService qrService) {
        this.qrService = qrService;
    }

    @GetMapping("/generate/{jobId}")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApiResponse<Map<String, String>>> generateQR(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserPrincipal principal) {
        String qrBase64 = qrService.generateQR(principal.getId(), jobId);
        Map<String, String> data = Map.of("qrCode", "data:image/png;base64," + qrBase64);
        return ResponseEntity.ok(ApiResponse.success("QR code generated", data));
    }

    @PostMapping("/scan")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> scanQR(
            @Valid @RequestBody QRScanRequest scanRequest) {
        Map<String, Object> result = qrService.scanQR(scanRequest.getQrToken());
        return ResponseEntity.ok(ApiResponse.success("QR scanned successfully", result));
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyToken(@PathVariable String token) {
        Map<String, Object> result = qrService.verifyByToken(token);
        return ResponseEntity.ok(ApiResponse.success("Verification complete", result));
    }

    @PostMapping("/attendance/{token}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> recordAttendance(@PathVariable String token) {
        Map<String, Object> result = qrService.scanQR(token);
        return ResponseEntity.ok(ApiResponse.success("Attendance recorded", result));
    }

    /** Resend QR code via WhatsApp for an approved application */
    @PostMapping("/resend/{applicationId}")
    @PreAuthorize("hasRole('WORKER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resendQR(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserPrincipal principal) {
        qrService.resendQR(applicationId, principal.getId(), principal.getRole());
        return ResponseEntity.ok(ApiResponse.success("QR code resent via WhatsApp"));
    }
}
