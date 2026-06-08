package com.flexiwork.dto;

import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull(message = "paymentId is required")
    private Long paymentId;

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
}
