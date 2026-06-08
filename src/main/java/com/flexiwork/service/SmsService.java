package com.flexiwork.service;

import com.flexiwork.model.Job;
import com.flexiwork.model.PendingNotification;
import com.flexiwork.model.User;
import com.flexiwork.repository.PendingNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);
    private static final String WHATSAPP_SERVICE = "http://localhost:3001";
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final int MAX_RETRIES = 5;

    private final RestTemplate restTemplate = new RestTemplate();
    private final PendingNotificationRepository pendingRepo;

    public SmsService(PendingNotificationRepository pendingRepo) {
        this.pendingRepo = pendingRepo;
    }

    /** Check if WhatsApp service is up and client is connected */
    public boolean isWhatsAppReady() {
        try {
            ResponseEntity<Map> resp = restTemplate.getForEntity(WHATSAPP_SERVICE + "/health", Map.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                Object ready = resp.getBody().get("clientReady");
                return Boolean.TRUE.equals(ready);
            }
        } catch (Exception e) {
            log.debug("WhatsApp health check failed: {}", e.getMessage());
        }
        return false;
    }

    /** Retry pending notifications — runs every 60 seconds */
    @Scheduled(fixedDelay = 60000)
    public void retryPendingNotifications() {
        if (!isWhatsAppReady()) return;
        List<PendingNotification> pending = pendingRepo.findByStatus("PENDING");
        for (PendingNotification n : pending) {
            try {
                Map<String, String> payload = new HashMap<>();
                payload.put("phone",   n.getPhone());
                payload.put("message", n.getMessage());
                if (n.getQrBase64() != null) payload.put("qrBase64", n.getQrBase64());
                boolean sent = postDirect(n.getEndpoint(), payload);
                if (sent) {
                    n.setStatus("SENT");
                    n.setSentAt(LocalDateTime.now());
                } else {
                    n.setRetryCount(n.getRetryCount() + 1);
                    if (n.getRetryCount() >= MAX_RETRIES) n.setStatus("FAILED");
                }
            } catch (Exception e) {
                n.setRetryCount(n.getRetryCount() + 1);
                if (n.getRetryCount() >= MAX_RETRIES) n.setStatus("FAILED");
            }
            pendingRepo.save(n);
        }
    }

    // ── Check-In ──
    public void sendCheckInNotification(User user, Job job) {
        String time = LocalDateTime.now().format(TIME_FMT);
        Map<String, String> body = new HashMap<>();
        body.put("phone",      user.getPhone());
        body.put("workerName", user.getFullName());
        body.put("jobTitle",   job.getTitle());
        body.put("location",   job.getFactoryLocation() != null ? job.getFactoryLocation() : job.getDistrict());
        body.put("time",       time);
        post("/checkin", body);
    }

    // ── Check-Out ──
    public void sendCheckOutNotification(User user, Job job) {
        String time = LocalDateTime.now().format(TIME_FMT);
        Map<String, String> body = new HashMap<>();
        body.put("phone",      user.getPhone());
        body.put("workerName", user.getFullName());
        body.put("jobTitle",   job.getTitle());
        body.put("location",   job.getFactoryLocation() != null ? job.getFactoryLocation() : job.getDistrict());
        body.put("time",       time);
        post("/checkout", body);
    }

    // ── Approval ──
    public void sendApprovalNotification(User user, Job job) {
        String date = job.getShiftDate() != null ? job.getShiftDate().format(DATE_FMT) : "TBD";
        String wage = job.getDailyWage() != null ? String.format("LKR %,.2f", job.getDailyWage()) : "N/A";
        String message =
            "🎉 *FlexiWork — Application Approved!*\n\n" +
            "Hello *" + user.getFullName() + "*,\n\n" +
            "Your application has been *APPROVED* ✅\n\n" +
            "📌 Job: " + job.getTitle() + "\n" +
            "🏭 Location: " + (job.getFactoryLocation() != null ? job.getFactoryLocation() : job.getDistrict()) + "\n" +
            "📅 Shift Date: " + date + "\n" +
            "💵 Daily Wage: " + wage + "\n\n" +
            "Open the FlexiWork app to get your QR code for check-in. 📱";

        Map<String, String> body = new HashMap<>();
        body.put("phone",   user.getPhone());
        body.put("message", message);
        post("/send", body);
    }

    // ── Shift Reminder ──
    public void sendShiftReminder(User user, Job job) {
        String date      = job.getShiftDate() != null ? job.getShiftDate().format(DATE_FMT) : "Tomorrow";
        String startTime = job.getShiftStartTime() != null ? job.getShiftStartTime().format(TIME_FMT) : "";
        String message =
            "⏰ *FlexiWork — Shift Reminder!*\n\n" +
            "Hello *" + user.getFullName() + "*,\n\n" +
            "Don't forget your shift!\n\n" +
            "📌 Job: " + job.getTitle() + "\n" +
            "🏭 Location: " + (job.getFactoryLocation() != null ? job.getFactoryLocation() : job.getDistrict()) + "\n" +
            "📅 Date: " + date + "\n" +
            "🕐 Start Time: " + startTime + "\n\n" +
            "Open the app to get your QR code before arriving. Good luck! 👷";

        Map<String, String> body = new HashMap<>();
        body.put("phone",   user.getPhone());
        body.put("message", message);
        post("/send", body);
    }

    // ── Password Reset OTP ──
    public void sendPasswordResetOtp(User user, String otp) {
        String message =
            "🔐 *FlexiWork — Password Reset*\n\n" +
            "Hello *" + user.getFullName() + "*,\n\n" +
            "Your OTP code is:\n\n" +
            "  *" + otp + "*\n\n" +
            "Enter this code to reset your password.\n" +
            "⚠️ Do NOT share this with anyone.\n\n" +
            "If you did not request this, ignore this message.";

        Map<String, String> body = new HashMap<>();
        body.put("phone",   user.getPhone());
        body.put("message", message);
        post("/send", body);
    }

    // ── Custom message (used for shift extensions etc.) ──
    public void sendCustomMessage(User user, String message) {
        Map<String, String> body = new HashMap<>();
        body.put("phone",   user.getPhone());
        body.put("message", message);
        post("/send", body);
    }

    // ── Resend QR ──
    public void sendQRCode(User user, Job job, String qrBase64) {
        String date = job.getShiftDate() != null ? job.getShiftDate().format(DATE_FMT) : "TBD";
        String message =
            "📲 *FlexiWork — Your QR Code*\n\n" +
            "Hello *" + user.getFullName() + "*,\n\n" +
            "Here is your QR code for:\n" +
            "📌 " + job.getTitle() + " | 📅 " + date + "\n\n" +
            "Show this to the security guard at the gate for check-in/check-out. 🏭";

        Map<String, String> body = new HashMap<>();
        body.put("phone",    user.getPhone());
        body.put("message",  message);
        body.put("qrBase64", qrBase64);
        post("/send-qr", body);
    }

    // ── Job Cancelled ──
    public void sendJobCancelledNotification(User user, Job job) {
        String date = job.getShiftDate() != null ? job.getShiftDate().format(DATE_FMT) : "N/A";
        String message =
            "❌ *FlexiWork — Job Cancelled!*\n\n" +
            "Hello *" + user.getFullName() + "*,\n\n" +
            "Unfortunately the following job has been *cancelled*:\n\n" +
            "📌 Job: " + job.getTitle() + "\n" +
            "🏭 Location: " + (job.getFactoryLocation() != null ? job.getFactoryLocation() : job.getDistrict()) + "\n" +
            "📅 Shift Date: " + date + "\n\n" +
            "We're sorry for the inconvenience. Please check the app for other available jobs. 🙏";

        Map<String, String> body = new HashMap<>();
        body.put("phone",   user.getPhone());
        body.put("message", message);
        post("/send", body);
    }

    // ── HTTP helpers ──
    private void post(String path, Map<String, String> payload) {
        if (!isWhatsAppReady()) {
            // Save to pending_notifications for retry
            PendingNotification pending = new PendingNotification();
            pending.setPhone(payload.getOrDefault("phone", ""));
            pending.setMessage(payload.getOrDefault("message", ""));
            pending.setQrBase64(payload.get("qrBase64"));
            pending.setEndpoint(path);
            pendingRepo.save(pending);
            log.warn("WhatsApp down — queued notification for {} at {}", payload.get("phone"), path);
            return;
        }
        postDirect(path, payload);
    }

    private boolean postDirect(String path, Map<String, String> payload) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                WHATSAPP_SERVICE + path, entity, String.class);
            log.info("WhatsApp [{}] → {}", path, response.getBody());
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("WhatsApp send failed ({}): {}", path, e.getMessage());
            return false;
        }
    }
}
