package com.flexiwork.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Central email service for all FlexiWork notifications.
 * All methods are @Async — emails never block the HTTP request.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${admin.email}")
    private String adminEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ── Worker document lifecycle ─────────────────────────────────────────────

    /** Sent to worker immediately after successful registration */
    @Async
    public void sendWorkerWelcome(String to, String firstName) {
        send(to,
            "Welcome to FlexiWork, " + firstName + "! 🎉",
            "Hi " + firstName + ",\n\n" +
            "Your FlexiWork worker account has been successfully created!\n\n" +
            "Next step: Please log in and upload your NIC documents so we can verify your account.\n\n" +
            "👉 Log in here: " + baseUrl + "\n\n" +
            "Once verified, you'll be able to browse and apply for jobs.\n\n" +
            "— FlexiWork Team"
        );
    }

    /** Sent to worker immediately when they upload NIC documents */
    @Async
    public void sendWorkerDocSubmitted(String to, String firstName) {
        send(to,
            "FlexiWork — Documents Received ✅",
            "Hi " + firstName + ",\n\n" +
            "We have received your NIC documents for verification.\n\n" +
            "⏳ Status: Under Review\n" +
            "⏰ Expected: Approved within 24 hours\n\n" +
            "You will receive another email once your documents are reviewed.\n" +
            "If you are not approved within 24 hours, you will be automatically approved.\n\n" +
            "— FlexiWork Team\n" + baseUrl
        );
    }

    /** Sent to worker when admin or scheduler approves their documents */
    @Async
    public void sendWorkerDocApproved(String to, String firstName) {
        send(to,
            "FlexiWork — Documents Approved 🎉",
            "Hi " + firstName + ",\n\n" +
            "Great news! Your NIC documents have been verified and approved.\n\n" +
            "✅ You can now apply for jobs on FlexiWork.\n\n" +
            "👉 Browse Jobs: " + baseUrl + "/index.html\n\n" +
            "— FlexiWork Team"
        );
    }

    /** Sent to worker when admin rejects their documents */
    @Async
    public void sendWorkerDocRejected(String to, String firstName, String reason) {
        send(to,
            "FlexiWork — Documents Rejected ❌",
            "Hi " + firstName + ",\n\n" +
            "Unfortunately, your NIC documents were not approved.\n\n" +
            "❌ Reason: " + (reason != null ? reason : "Documents were not clear enough") + "\n\n" +
            "Please log in and resubmit clearer photos of your NIC.\n" +
            "👉 " + baseUrl + "/worker-dashboard.html\n\n" +
            "If you believe this is a mistake, please contact support.\n\n" +
            "— FlexiWork Team"
        );
    }

    // ── Company document lifecycle ─────────────────────────────────────────────

    /** Sent to company immediately after successful registration */
    @Async
    public void sendCompanyWelcome(String to, String companyName) {
        send(to,
            "Welcome to FlexiWork — Company Registered 🎉",
            "Hi " + companyName + ",\n\n" +
            "Your company has been successfully registered on FlexiWork!\n\n" +
            "Next step: Please log in and upload your Business Registration Certificate so we can verify your account.\n\n" +
            "👉 Log in here: " + baseUrl + "\n\n" +
            "Once verified, you'll be able to post jobs and hire workers.\n\n" +
            "— FlexiWork Team"
        );
    }

    /** Sent to company immediately when they upload BR certificate */
    @Async
    public void sendCompanyDocSubmitted(String to, String companyName) {
        send(to,
            "FlexiWork — Certificate Received ✅",
            "Hi " + companyName + ",\n\n" +
            "We have received your Business Registration Certificate for verification.\n\n" +
            "⏳ Status: Under Review\n" +
            "⏰ Expected: Approved within 24 hours\n\n" +
            "You will receive another email once your certificate is reviewed.\n" +
            "If you are not approved within 24 hours, you will be automatically approved.\n\n" +
            "— FlexiWork Team\n" + baseUrl
        );
    }

    /** Sent to company when admin or scheduler approves their certificate */
    @Async
    public void sendCompanyDocApproved(String to, String companyName) {
        send(to,
            "FlexiWork — Company Verified 🎉",
            "Hi " + companyName + ",\n\n" +
            "Your Business Registration Certificate has been verified and approved.\n\n" +
            "✅ You can now post jobs and hire workers on FlexiWork.\n\n" +
            "👉 Go to Dashboard: " + baseUrl + "/employer-dashboard.html\n\n" +
            "— FlexiWork Team"
        );
    }

    /** Sent to company when admin rejects their certificate */
    @Async
    public void sendCompanyDocRejected(String to, String companyName, String reason) {
        send(to,
            "FlexiWork — Certificate Rejected ❌",
            "Hi " + companyName + ",\n\n" +
            "Unfortunately, your Business Registration Certificate was not approved.\n\n" +
            "❌ Reason: " + (reason != null ? reason : "Certificate was not clear enough") + "\n\n" +
            "Please log in and resubmit a clearer image of your BR certificate.\n" +
            "👉 " + baseUrl + "/employer-dashboard.html\n\n" +
            "If you believe this is a mistake, please contact support.\n\n" +
            "— FlexiWork Team"
        );
    }

    // ── Admin alerts ──────────────────────────────────────────────────────────

    /** Sent to admin when any new document is submitted for review */
    @Async
    public void sendAdminDocAlert(String submitterName, String type) {
        send(adminEmail,
            "FlexiWork Admin — New " + type + " Document Pending",
            "A new document is waiting for your review.\n\n" +
            "👤 Submitted by: " + submitterName + "\n" +
            "📄 Type: " + type + "\n\n" +
            "👉 Review now: " + baseUrl + "/admin.html\n\n" +
            "Note: If not reviewed within 24 hours, it will be automatically approved.\n\n" +
            "— FlexiWork System"
        );
    }

    // ── Password reset ─────────────────────────────────────────────────────────

    /** Sent when a user requests a password reset OTP */
    @Async
    public void sendPasswordResetOtp(String to, String otp) {
        send(to,
            "FlexiWork — Password Reset OTP",
            "Hello,\n\n" +
            "Your FlexiWork password reset OTP is:\n\n" +
            "  " + otp + "\n\n" +
            "This code is valid for 10 minutes. Do not share it with anyone.\n\n" +
            "If you did not request a password reset, please ignore this email.\n\n" +
            "— FlexiWork Team"
        );
    }

    // ── Internal helper ───────────────────────────────────────────────────────

    private void send(String to, String subject, String text) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("FlexiWork <" + fromEmail + ">");
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(text);
            mailSender.send(msg);
            log.info("[Email] Sent '{}' → {}", subject, to);
        } catch (Exception e) {
            log.error("[Email] Failed to send '{}' → {}: {}", subject, to, e.getMessage());
        }
    }
}
