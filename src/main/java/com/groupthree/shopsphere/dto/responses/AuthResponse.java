package com.groupthree.shopsphere.dto.responses;

public class AuthResponse {
    public String status;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(String status, String message) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
