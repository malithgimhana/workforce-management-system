package com.flexiwork.controller;

import com.flexiwork.dto.ApiResponse;
import com.flexiwork.model.QRVerification;
import com.flexiwork.model.UserPrincipal;
import com.flexiwork.repository.CommissionPaymentRepository;
import com.flexiwork.repository.QRVerificationRepository;
import com.flexiwork.service.ReportExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final QRVerificationRepository qrVerificationRepository;
    private final CommissionPaymentRepository commissionPaymentRepository;
    private final ReportExportService reportExportService;

    public ReportController(QRVerificationRepository qrVerificationRepository,
                            CommissionPaymentRepository commissionPaymentRepository,
                            ReportExportService reportExportService) {
        this.qrVerificationRepository = qrVerificationRepository;
        this.commissionPaymentRepository = commissionPaymentRepository;
        this.reportExportService = reportExportService;
    }

    @GetMapping("/attendance")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<QRVerification>>> getAttendance(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Long companyId = principal.getCompanyId();
        int safeSize = Math.min(size, 200);
        List<QRVerification> records = qrVerificationRepository.findByJobCompanyCompanyId(companyId)
                .stream()
                .skip((long) page * safeSize)
                .limit(safeSize)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Attendance records retrieved", records));
    }

    @GetMapping("/commission")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<?> getCommission(@AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        var payments = commissionPaymentRepository.findByCompanyCompanyId(companyId);
        return ResponseEntity.ok(ApiResponse.success("Commission records retrieved", payments));
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<byte[]> exportCsv(@RequestParam String type,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        byte[] data;
        String filename;

        if ("attendance".equalsIgnoreCase(type)) {
            data = reportExportService.exportAttendanceCsv(companyId);
            filename = "attendance_report.csv";
        } else if ("commission".equalsIgnoreCase(type)) {
            data = reportExportService.exportCommissionCsv();
            filename = "commission_report.csv";
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('EMPLOYER', 'ADMIN')")
    public ResponseEntity<byte[]> exportPdf(@RequestParam String type,
                                             @AuthenticationPrincipal UserPrincipal principal) {
        Long companyId = principal.getCompanyId();
        byte[] data;
        String filename;

        if ("attendance".equalsIgnoreCase(type)) {
            data = reportExportService.exportAttendancePdf(companyId);
            filename = "attendance_report.pdf";
        } else if ("commission".equalsIgnoreCase(type)) {
            data = reportExportService.exportCommissionPdf();
            filename = "commission_report.pdf";
        } else {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}
