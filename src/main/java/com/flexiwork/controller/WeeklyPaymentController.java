package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.model.WeeklyPaymentReceipt;
import com.flexiwork.service.AuditLogService;
import com.flexiwork.service.WeeklyPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class WeeklyPaymentController {

    private final WeeklyPaymentService paymentService;
    private final AuditLogService auditLogService;

    public WeeklyPaymentController(WeeklyPaymentService paymentService, AuditLogService auditLogService) {
        this.paymentService = paymentService;
        this.auditLogService = auditLogService;
    }

    /** Generate or retrieve a weekly receipt. weekDate = any day in the target week. */
    @GetMapping("/receipt")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<WeeklyPaymentReceipt>> getReceipt(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekDate,
            @AuthenticationPrincipal UserPrincipal principal) {
        WeeklyPaymentReceipt receipt = paymentService.generateReceipt(
            principal.getCompanyId(), weekDate);
        return ResponseEntity.ok(ApiResponse.success("Weekly receipt", receipt));
    }

    /** Pay a receipt with fake card details */
    @PostMapping("/pay/{receiptId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<WeeklyPaymentReceipt>> pay(
            @PathVariable Long receiptId,
            @RequestBody Map<String, String> cardDetails,
            @AuthenticationPrincipal UserPrincipal principal,
            HttpServletRequest req) {
        WeeklyPaymentReceipt receipt = paymentService.payReceipt(receiptId, cardDetails);
        auditLogService.log(principal.getId(), "EMPLOYER", "PAYMENT_MADE",
            "WeeklyPaymentReceipt", receiptId,
            "Paid receipt for week " + receipt.getWeekStart() + " — LKR " + receipt.getTotalCommission(),
            req.getRemoteAddr());
        return ResponseEntity.ok(ApiResponse.success("Payment successful! Commission paid.", receipt));
    }

    /** Employer's payment history */
    @GetMapping("/my-receipts")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ApiResponse<List<WeeklyPaymentReceipt>>> myReceipts(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<WeeklyPaymentReceipt> receipts = paymentService.getCompanyReceipts(principal.getCompanyId());
        return ResponseEntity.ok(ApiResponse.success("Payment receipts", receipts));
    }

    /** Admin — all payments */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WeeklyPaymentReceipt>>> allReceipts() {
        return ResponseEntity.ok(ApiResponse.success("All receipts", paymentService.getAllReceipts()));
    }
}
