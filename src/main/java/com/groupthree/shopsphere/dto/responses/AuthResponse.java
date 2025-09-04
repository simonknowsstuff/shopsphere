package com.groupthree.shopsphere.dto.responses;

public class AuthResponse {
    private String status;
    private String message;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String status, String message, String role) {
        this.message = message;
        this.status = status;
        this.role = role;
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

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
