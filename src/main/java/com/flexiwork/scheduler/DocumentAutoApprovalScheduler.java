package com.flexiwork.scheduler;

import com.flexiwork.enums.DocumentStatus;
import com.flexiwork.model.Company;
import com.flexiwork.model.User;
import com.flexiwork.repository.CompanyRepository;
import com.flexiwork.repository.UserRepository;
import com.flexiwork.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Automatically approves documents that have been PENDING for more than
 * app.doc.auto-approve-hours hours without admin action.
 * Runs every hour.
 */
@Component
public class DocumentAutoApprovalScheduler {

    private static final Logger log = LoggerFactory.getLogger(DocumentAutoApprovalScheduler.class);

    private final UserRepository    userRepository;
    private final CompanyRepository companyRepository;
    private final EmailService      emailService;

    @Value("${app.doc.auto-approve-hours:24}")
    private int autoApproveHours;

    public DocumentAutoApprovalScheduler(UserRepository userRepository,
                                         CompanyRepository companyRepository,
                                         EmailService emailService) {
        this.userRepository    = userRepository;
        this.companyRepository = companyRepository;
        this.emailService      = emailService;
    }

    // Runs every hour at the top of the hour
    @Scheduled(cron = "0 0 * * * *")
    public void autoApproveDocuments() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(autoApproveHours);
        log.info("[DocAutoApprove] Running — cutoff: {} ({} hours)", cutoff, autoApproveHours);

        approveWorkers(cutoff);
        approveCompanies(cutoff);
    }

    private void approveWorkers(LocalDateTime cutoff) {
        List<User> pending = userRepository
                .findByDocStatusAndDocSubmittedAtBefore(DocumentStatus.PENDING, cutoff);

        if (pending.isEmpty()) {
            log.info("[DocAutoApprove] No pending worker documents to auto-approve");
            return;
        }

        for (User user : pending) {
            user.setDocStatus(DocumentStatus.APPROVED);
            userRepository.save(user);
            log.info("[DocAutoApprove] Worker approved: {} ({})", user.getFullName(), user.getEmail());

            emailService.sendWorkerDocApproved(user.getEmail(), user.getFirstName());
        }

        log.info("[DocAutoApprove] Auto-approved {} worker(s)", pending.size());
    }

    private void approveCompanies(LocalDateTime cutoff) {
        List<Company> pending = companyRepository
                .findByDocStatusAndDocSubmittedAtBefore(DocumentStatus.PENDING, cutoff);

        if (pending.isEmpty()) {
            log.info("[DocAutoApprove] No pending company documents to auto-approve");
            return;
        }

        for (Company company : pending) {
            company.setDocStatus(DocumentStatus.APPROVED);
            companyRepository.save(company);
            log.info("[DocAutoApprove] Company approved: {} ({})", company.getName(), company.getEmail());

            emailService.sendCompanyDocApproved(company.getEmail(), company.getName());
        }

        log.info("[DocAutoApprove] Auto-approved {} company/companies", pending.size());
    }

}
