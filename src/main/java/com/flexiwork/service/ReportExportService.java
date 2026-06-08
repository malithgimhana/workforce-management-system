package com.flexiwork.service;

import com.flexiwork.model.CommissionPayment;
import com.flexiwork.model.QRVerification;
import com.flexiwork.repository.CommissionPaymentRepository;
import com.flexiwork.repository.JobRepository;
import com.flexiwork.repository.QRVerificationRepository;
import com.opencsv.CSVWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportExportService {

    private static final Logger log = LoggerFactory.getLogger(ReportExportService.class);

    private final QRVerificationRepository qrVerificationRepository;
    private final CommissionPaymentRepository commissionPaymentRepository;
    private final JobRepository jobRepository;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ReportExportService(QRVerificationRepository qrVerificationRepository,
                               CommissionPaymentRepository commissionPaymentRepository,
                               JobRepository jobRepository) {
        this.qrVerificationRepository = qrVerificationRepository;
        this.commissionPaymentRepository = commissionPaymentRepository;
        this.jobRepository = jobRepository;
    }

    public byte[] exportAttendanceCsv(Long companyId) {
        List<QRVerification> records = qrVerificationRepository.findAll().stream()
                .filter(qr -> qr.getJob() != null
                        && qr.getJob().getCompany() != null
                        && qr.getJob().getCompany().getCompanyId().equals(companyId))
                .toList();

        try (StringWriter sw = new StringWriter(); CSVWriter writer = new CSVWriter(sw)) {
            writer.writeNext(new String[]{"Worker Name", "NIC", "Job Title", "Shift Date",
                    "Check In", "Check Out", "Status"});
            for (QRVerification qr : records) {
                writer.writeNext(new String[]{
                        qr.getUser().getFullName(),
                        qr.getUser().getNic(),
                        qr.getJob().getTitle(),
                        qr.getJob().getShiftDate() != null ? qr.getJob().getShiftDate().toString() : "",
                        qr.getCheckInTime() != null ? qr.getCheckInTime().format(DTF) : "",
                        qr.getCheckOutTime() != null ? qr.getCheckOutTime().format(DTF) : "",
                        qr.getIsVerified() ? "VERIFIED" : "PENDING"
                });
            }
            return sw.toString().getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV", e);
        }
    }

    public byte[] exportAttendancePdf(Long companyId) {
        List<QRVerification> records = qrVerificationRepository.findAll().stream()
                .filter(qr -> qr.getJob() != null
                        && qr.getJob().getCompany() != null
                        && qr.getJob().getCompany().getCompanyId().equals(companyId))
                .toList();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                content.beginText();
                content.setFont(boldFont, 16);
                content.newLineAtOffset(50, 780);
                content.showText("Attendance Report");
                content.endText();

                float y = 750;
                content.beginText();
                content.setFont(boldFont, 10);
                content.newLineAtOffset(50, y);
                content.showText(String.format("%-25s %-20s %-20s %-20s", "Worker", "Job", "Check In", "Check Out"));
                content.endText();

                y -= 15;
                for (QRVerification qr : records) {
                    if (y < 50) break;
                    content.beginText();
                    content.setFont(regularFont, 9);
                    content.newLineAtOffset(50, y);
                    String line = String.format("%-25s %-20s %-20s %-20s",
                            truncate(qr.getUser().getFullName(), 24),
                            truncate(qr.getJob().getTitle(), 19),
                            qr.getCheckInTime() != null ? qr.getCheckInTime().format(DTF) : "N/A",
                            qr.getCheckOutTime() != null ? qr.getCheckOutTime().format(DTF) : "N/A"
                    );
                    content.showText(line);
                    content.endText();
                    y -= 13;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export PDF", e);
        }
    }

    public byte[] exportCommissionCsv() {
        List<CommissionPayment> payments = commissionPaymentRepository.findAll();

        try (StringWriter sw = new StringWriter(); CSVWriter writer = new CSVWriter(sw)) {
            writer.writeNext(new String[]{"Payment ID", "Company", "Job Title", "Worker Wage",
                    "Commission Amount", "Status", "Created At", "Paid At"});
            for (CommissionPayment p : payments) {
                writer.writeNext(new String[]{
                        String.valueOf(p.getPaymentId()),
                        p.getCompany().getName(),
                        p.getJob().getTitle(),
                        p.getWorkerWage().toString(),
                        p.getCommissionAmount().toString(),
                        p.getStatus().toString(),
                        p.getCreatedAt() != null ? p.getCreatedAt().format(DTF) : "",
                        p.getPaidAt() != null ? p.getPaidAt().format(DTF) : ""
                });
            }
            return sw.toString().getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export commission CSV", e);
        }
    }

    public byte[] exportCommissionPdf() {
        List<CommissionPayment> payments = commissionPaymentRepository.findAll();

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                content.beginText();
                content.setFont(boldFont, 16);
                content.newLineAtOffset(50, 780);
                content.showText("Commission Payment Report");
                content.endText();

                float y = 750;
                content.beginText();
                content.setFont(boldFont, 10);
                content.newLineAtOffset(50, y);
                content.showText(String.format("%-20s %-20s %-15s %-15s %-10s",
                        "Company", "Job", "Wage", "Commission", "Status"));
                content.endText();

                y -= 15;
                for (CommissionPayment p : payments) {
                    if (y < 50) break;
                    content.beginText();
                    content.setFont(regularFont, 9);
                    content.newLineAtOffset(50, y);
                    String line = String.format("%-20s %-20s %-15s %-15s %-10s",
                            truncate(p.getCompany().getName(), 19),
                            truncate(p.getJob().getTitle(), 19),
                            p.getWorkerWage().toString(),
                            p.getCommissionAmount().toString(),
                            p.getStatus().toString()
                    );
                    content.showText(line);
                    content.endText();
                    y -= 13;
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export commission PDF", e);
        }
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() > maxLen ? s.substring(0, maxLen) : s;
    }
}
