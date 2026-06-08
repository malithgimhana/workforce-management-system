package com.flexiwork.dto;

import jakarta.validation.constraints.NotBlank;

public class QRScanRequest {
    @NotBlank(message = "QR token is required")
    private String qrToken;

    public QRScanRequest() {}

    public QRScanRequest(String qrToken) {
        this.qrToken = qrToken;
    }

    public String getQrToken() { return qrToken; }
    public void setQrToken(String qrToken) { this.qrToken = qrToken; }
}
