package com.flexiwork.service;

import com.flexiwork.enums.ApplicationStatus;
import com.flexiwork.enums.PaymentStatus;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.*;
import com.flexiwork.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WeeklyPaymentService {

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("10.00"); // 10%
    private static final ZoneId SL_ZONE = ZoneId.of("Asia/Colombo");

    private final WeeklyPaymentReceiptRepository receiptRepo;
    private final CommissionPaymentRepository commissionRepo;
    private final ApplicationRepository applicationRepo;
    private final CompanyRepository companyRepo;
    private final JobRepository jobRepo;

    public WeeklyPaymentService(WeeklyPaymentReceiptRepository receiptRepo,
                                CommissionPaymentRepository commissionRepo,
                                ApplicationRepository applicationRepo,
                                CompanyRepository companyRepo,
                                JobRepository jobRepo) {
        this.receiptRepo = receiptRepo;
        this.commissionRepo = commissionRepo;
        this.applicationRepo = applicationRepo;
        this.companyRepo = companyRepo;
        this.jobRepo = jobRepo;
    }

    /**
     * Generate or retrieve a weekly receipt for a company.
     * weekStart must be a Monday (auto-corrected if not).
     */
    @Transactional
    public WeeklyPaymentReceipt generateReceipt(Long companyId, LocalDate anyDayInWeek) {
        LocalDate weekStart = anyDayInWeek.with(DayOfWeek.MONDAY);
        LocalDate weekEnd   = weekStart.plusDays(6); // Sunday

        // Return existing if already generated
        return receiptRepo.findByCompanyCompanyIdAndWeekStart(companyId, weekStart)
                .orElseGet(() -> buildNewReceipt(companyId, weekStart, weekEnd));
    }

    private WeeklyPaymentReceipt buildNewReceipt(Long companyId, LocalDate weekStart, LocalDate weekEnd) {
        Company company = companyRepo.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company", companyId));

        // Find all approved applications for this company in the week
        List<Application> apps = applicationRepo.findByJobCompanyCompanyIdAndStatus(
            companyId, ApplicationStatus.APPROVED);

        List<CommissionPayment> lineItems = new ArrayList<>();
        BigDecimal totalWages = BigDecimal.ZERO;

        for (Application app : apps) {
            LocalDate shiftDate = app.getJob().getShiftDate();
            if (shiftDate == null) continue;
            if (shiftDate.isBefore(weekStart) || shiftDate.isAfter(weekEnd)) continue;

            BigDecimal wage = app.getJob().getDailyWage() != null
                ? app.getJob().getDailyWage() : BigDecimal.ZERO;
            BigDecimal commission = wage.multiply(COMMISSION_RATE)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Check if commission payment already recorded
            List<CommissionPayment> existing = commissionRepo.findByJobJobId(app.getJob().getJobId());
            CommissionPayment cp;
            if (!existing.isEmpty()) {
                cp = existing.get(0);
            } else {
                cp = CommissionPayment.builder()
                    .company(company)
                    .job(app.getJob())
                    .workerWage(wage)
                    .commissionAmount(commission)
                    .status(PaymentStatus.PENDING)
                    .build();
                cp = commissionRepo.save(cp);
            }
            lineItems.add(cp);
            totalWages = totalWages.add(wage);
        }

        BigDecimal totalCommission = totalWages.multiply(COMMISSION_RATE)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        WeeklyPaymentReceipt receipt = new WeeklyPaymentReceipt();
        receipt.setCompany(company);
        receipt.setWeekStart(weekStart);
        receipt.setWeekEnd(weekEnd);
        receipt.setTotalWorkerWages(totalWages);
        receipt.setCommissionRate(COMMISSION_RATE);
        receipt.setTotalCommission(totalCommission);
        receipt.setPaymentStatus("PENDING");
        receipt.setLineItems(lineItems);
        return receiptRepo.save(receipt);
    }

    /**
     * Process fake card payment.
     */
    @Transactional
    public WeeklyPaymentReceipt payReceipt(Long receiptId, Map<String, String> cardDetails) {
        WeeklyPaymentReceipt receipt = receiptRepo.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt", receiptId));

        if ("PAID".equals(receipt.getPaymentStatus())) {
            throw new BusinessException("This receipt has already been paid");
        }

        // Fake card validation (prototype)
        String cardNumber = cardDetails.getOrDefault("cardNumber", "").replaceAll("\\s", "");
        String expiry     = cardDetails.getOrDefault("expiry", "");
        String cvv        = cardDetails.getOrDefault("cvv", "");

        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            throw new BusinessException("Invalid card number — must be 16 digits");
        }
        if (!expiry.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new BusinessException("Invalid expiry format — use MM/YY");
        }
        if (cvv.length() != 3 || !cvv.matches("\\d+")) {
            throw new BusinessException("Invalid CVV — must be 3 digits");
        }

        // Mark receipt as paid
        receipt.setPaymentStatus("PAID");
        receipt.setCardLast4(cardNumber.substring(12));
        receipt.setPaidAt(LocalDateTime.now(SL_ZONE));

        // Mark all line item commissions as paid
        if (receipt.getLineItems() != null) {
            for (CommissionPayment cp : receipt.getLineItems()) {
                cp.setStatus(PaymentStatus.PAID);
                cp.setPaidAt(LocalDateTime.now(SL_ZONE));
                commissionRepo.save(cp);
            }
        }

        // Update company balance (track what FlexiWork has earned)
        Company company = receipt.getCompany();
        company.setBalance(company.getBalance().add(receipt.getTotalCommission()));
        companyRepo.save(company);

        return receiptRepo.save(receipt);
    }

    public List<WeeklyPaymentReceipt> getCompanyReceipts(Long companyId) {
        return receiptRepo.findByCompanyCompanyIdOrderByWeekStartDesc(companyId);
    }

    public List<WeeklyPaymentReceipt> getAllReceipts() {
        return receiptRepo.findAllByOrderByCreatedAtDesc();
    }
}
