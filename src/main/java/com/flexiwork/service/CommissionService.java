package com.flexiwork.service;

import com.flexiwork.enums.PaymentStatus;
import com.flexiwork.model.CommissionPayment;
import com.flexiwork.model.Company;
import com.flexiwork.model.QRVerification;
import com.flexiwork.repository.CommissionPaymentRepository;
import com.flexiwork.repository.CompanyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CommissionService {

    private static final Logger log = LoggerFactory.getLogger(CommissionService.class);

    private final CommissionPaymentRepository commissionPaymentRepository;
    private final CompanyRepository companyRepository;

    private static final BigDecimal COMMISSION_RATE = new BigDecimal("0.10");

    public CommissionService(CommissionPaymentRepository commissionPaymentRepository,
                             CompanyRepository companyRepository) {
        this.commissionPaymentRepository = commissionPaymentRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public CommissionPayment calculateCommission(QRVerification verification) {
        BigDecimal wage = verification.getJob().getDailyWage();
        BigDecimal commission = wage.multiply(COMMISSION_RATE).setScale(2, RoundingMode.HALF_UP);

        CommissionPayment payment = CommissionPayment.builder()
                .company(verification.getJob().getCompany())
                .job(verification.getJob())
                .workerWage(wage)
                .commissionAmount(commission)
                .status(PaymentStatus.PENDING)
                .build();

        commissionPaymentRepository.save(payment);

        // Update company balance
        Company company = verification.getJob().getCompany();
        company.setBalance(company.getBalance().add(commission));
        companyRepository.save(company);

        log.info("Commission calculated: {} for job {} company {}",
                commission, verification.getJob().getJobId(), company.getCompanyId());

        return payment;
    }
}
