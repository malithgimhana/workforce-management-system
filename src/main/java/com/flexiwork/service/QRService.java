package com.flexiwork.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexiwork.enums.ApplicationStatus;
import com.flexiwork.enums.ScanType;
import com.flexiwork.exception.BusinessException;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.Application;
import com.flexiwork.model.Job;
import com.flexiwork.model.QRVerification;
import com.flexiwork.model.User;
import com.flexiwork.repository.ApplicationRepository;
import com.flexiwork.repository.JobRepository;
import com.flexiwork.repository.QRVerificationRepository;
import com.flexiwork.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;

@Service
public class QRService {

    private static final Logger log = LoggerFactory.getLogger(QRService.class);

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final QRVerificationRepository qrVerificationRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final CommissionService commissionService;
    private final SmsService smsService;
    private final ObjectMapper objectMapper;

    public QRService(QRVerificationRepository qrVerificationRepository,
                     ApplicationRepository applicationRepository,
                     UserRepository userRepository,
                     JobRepository jobRepository,
                     CommissionService commissionService,
                     SmsService smsService,
                     ObjectMapper objectMapper) {
        this.qrVerificationRepository = qrVerificationRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.commissionService = commissionService;
        this.smsService = smsService;
        this.objectMapper = objectMapper;
    }

    public String generateQR(Long userId, Long jobId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        // Check application is APPROVED
        Application application = applicationRepository.findByUserUserIdAndJobJobId(userId, jobId)
                .orElseThrow(() -> new BusinessException("No application found for this job"));

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new BusinessException("Application must be APPROVED to generate QR code");
        }

        // Get or create QR verification record
        QRVerification verification = qrVerificationRepository
                .findByUserUserIdAndJobJobId(userId, jobId)
                .orElseGet(() -> {
                    QRVerification v = QRVerification.builder()
                            .user(user)
                            .job(job)
                            .qrToken(generateQrToken())
                            .isVerified(false)
                            .build();
                    return qrVerificationRepository.save(v);
                });

        // Encode a URL so phone cameras open the verify page directly
        try {
            String qrContent = baseUrl + "/verify.html?token=" + verification.getQrToken();
            return generateQrBase64(qrContent);
        } catch (Exception e) {
            throw new BusinessException("Failed to generate QR code: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> scanQR(String qrToken) {
        QRVerification verification = qrVerificationRepository.findByQrToken(qrToken)
                .orElseThrow(() -> new ResourceNotFoundException("QR verification not found"));

        User user = verification.getUser();
        Job job = verification.getJob();

        Map<String, Object> result = new HashMap<>();
        result.put("userName", user.getFullName());
        result.put("jobTitle", job.getTitle());
        result.put("jobId", job.getJobId());
        result.put("userId", user.getUserId());

        if (verification.getCheckInTime() == null) {
            // First scan = CHECK_IN
            verification.setScanType(ScanType.CHECK_IN);
            verification.setCheckInTime(java.time.LocalDateTime.now());
            verification.setIsVerified(true);
            qrVerificationRepository.save(verification);

            smsService.sendCheckInNotification(user, job);

            result.put("action", "CHECK_IN");
            result.put("checkInTime", verification.getCheckInTime());
            result.put("message", "Check-in successful for " + user.getFullName());
        } else if (verification.getCheckOutTime() == null) {
            // Second scan = CHECK_OUT
            verification.setScanType(ScanType.CHECK_OUT);
            verification.setCheckOutTime(java.time.LocalDateTime.now());
            qrVerificationRepository.save(verification);

            // Trigger commission calculation
            commissionService.calculateCommission(verification);

            smsService.sendCheckOutNotification(user, job);

            result.put("action", "CHECK_OUT");
            result.put("checkInTime", verification.getCheckInTime());
            result.put("checkOutTime", verification.getCheckOutTime());
            result.put("message", "Check-out successful for " + user.getFullName());
        } else {
            result.put("action", "ALREADY_COMPLETED");
            result.put("checkInTime", verification.getCheckInTime());
            result.put("checkOutTime", verification.getCheckOutTime());
            result.put("message", "Worker has already checked in and out");
        }

        return result;
    }

    public Map<String, Object> verifyByToken(String token) {
        Map<String, Object> result = new HashMap<>();

        QRVerification verification = qrVerificationRepository.findByQrToken(token)
                .orElse(null);

        if (verification == null) {
            result.put("status", "NOT_FOUND");
            result.put("message", "Invalid QR code. Worker not found.");
            return result;
        }

        User user = verification.getUser();
        Job job = verification.getJob();

        // ── QR Expiry: valid only on shift date ──
        java.time.LocalDate today = java.time.LocalDate.now();
        if (job.getShiftDate() != null && !job.getShiftDate().equals(today)) {
            result.put("status", "EXPIRED");
            result.put("message", "QR code is only valid on shift date: " + job.getShiftDate());
            result.put("workerName", user.getFullName());
            result.put("nic",    user.getNic());
            result.put("phone",  user.getPhone());
            result.put("photo",  user.getPhoto());
            result.put("jobTitle",    job.getTitle());
            result.put("jobLocation", job.getFactoryLocation());
            result.put("shiftDate",   job.getShiftDate().toString());
            result.put("shiftStart",  job.getShiftStartTime() != null ? job.getShiftStartTime().toString() : null);
            result.put("shiftEnd",    job.getShiftEndTime()   != null ? job.getShiftEndTime().toString()   : null);
            return result;
        }

        boolean isActive = !Boolean.TRUE.equals(user.getIsDeleted());

        result.put("status", isActive ? "APPROVED" : "REJECTED");
        result.put("workerName", user.getFullName());
        result.put("nic", user.getNic());
        result.put("phone", user.getPhone());
        result.put("photo", user.getPhoto());
        result.put("district", user.getDistrict());
        result.put("jobTitle", job.getTitle());
        result.put("jobLocation", job.getFactoryLocation());
        result.put("shiftDate", job.getShiftDate() != null ? job.getShiftDate().toString() : null);
        result.put("shiftStart", job.getShiftStartTime() != null ? job.getShiftStartTime().toString() : null);
        result.put("shiftEnd",   job.getShiftEndTime()   != null ? job.getShiftEndTime().toString()   : null);
        result.put("checkInTime", verification.getCheckInTime());
        result.put("checkOutTime", verification.getCheckOutTime());
        result.put("isVerified", verification.getIsVerified());
        result.put("message", isActive ? "Worker is approved and active." : "Worker account is inactive or deactivated.");
        return result;
    }

    /** Resend QR code to the worker via WhatsApp */
    public void resendQR(Long applicationId, Long requesterId, String requesterRole) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application", applicationId));

        if (application.getStatus() != ApplicationStatus.APPROVED) {
            throw new BusinessException("QR can only be resent for approved applications");
        }

        // Security: worker can only resend their own QR
        if ("WORKER".equals(requesterRole) && !application.getUser().getUserId().equals(requesterId)) {
            throw new BusinessException("Not authorized to resend this QR");
        }

        Long userId = application.getUser().getUserId();
        Long jobId  = application.getJob().getJobId();

        QRVerification verification = qrVerificationRepository
                .findByUserUserIdAndJobJobId(userId, jobId)
                .orElseThrow(() -> new BusinessException("No QR code found for this application. Please contact admin."));

        try {
            String qrContent = baseUrl + "/verify.html?token=" + verification.getQrToken();
            String qrBase64 = generateQrBase64(qrContent);
            smsService.sendQRCode(application.getUser(), application.getJob(), qrBase64);
            log.info("QR resent for user={} job={}", userId, jobId);
        } catch (Exception e) {
            throw new BusinessException("Failed to resend QR: " + e.getMessage());
        }
    }

    private String generateQrToken() {
        return UUID.randomUUID().toString();
    }

    private String generateQrBase64(String content) throws WriterException, java.io.IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 300, 300, hints);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
