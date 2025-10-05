package com.groupthree.shopsphere.dto.responses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class PaymentResponse {
    @NotNull
    private String status;
    @NotEmpty
    private String message;

    private String paymentToken;

    public PaymentResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public PaymentResponse(String status, String message, String paymentToken) {
        this.status = status;
        this.message = message;
        this.paymentToken = paymentToken;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getPaymentToken() {
        return paymentToken;
    }
    public void setPaymentToken(String paymentToken) {
        this.paymentToken = paymentToken;
    }
}
