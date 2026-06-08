package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.dto.CommissionResponse;
import com.flexiwork.dto.PaymentRequest;
import com.flexiwork.enums.PaymentStatus;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.CommissionPayment;
import com.flexiwork.model.Company;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.CommissionPaymentRepository;
import com.flexiwork.service.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;  // used by getBalance response
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final CommissionPaymentRepository commissionPaymentRepository;
    private final CompanyService companyService;

    public PaymentController(CommissionPaymentRepository commissionPaymentRepository,
                             CompanyService companyService) {
        this.commissionPaymentRepository = commissionPaymentRepository;
        this.companyService = companyService;
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalance(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        Company company = companyService.findById(companyId);
        BigDecimal total = commissionPaymentRepository.sumCommissionByCompanyId(companyId);
        Map<String, Object> data = Map.of(
                "balance", company.getBalance(),
                "totalCommission", total != null ? total : BigDecimal.ZERO
        );
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved", data));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CommissionResponse>>> getHistory(
            @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        List<CommissionResponse> history = commissionPaymentRepository.findByCompanyCompanyId(companyId)
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Payment history retrieved", history));
    }

    @PostMapping("/pay")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CommissionResponse>> payCommission(
            @Valid @RequestBody PaymentRequest body) {
        Long paymentId = body.getPaymentId();
        CommissionPayment payment = commissionPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", paymentId));
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        commissionPaymentRepository.save(payment);
        return ResponseEntity.ok(ApiResponse.success("Payment marked as PAID", toResponse(payment)));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<CommissionResponse>>> getAllPayments() {
        List<CommissionResponse> payments = commissionPaymentRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("All payments retrieved", payments));
    }

    private CommissionResponse toResponse(CommissionPayment p) {
        return CommissionResponse.builder()
                .paymentId(p.getPaymentId())
                .companyId(p.getCompany().getCompanyId())
                .jobId(p.getJob().getJobId())
                .jobTitle(p.getJob().getTitle())
                .workerWage(p.getWorkerWage())
                .commissionAmount(p.getCommissionAmount())
                .status(p.getStatus())
                .paidAt(p.getPaidAt())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
