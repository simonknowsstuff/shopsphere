package com.groupthree.shopsphere.dto.responses;
import java.util.Set;
public class AuthResponse {
    private String status;
    private String message;
    private String token;
    private Set<String> role;

    public AuthResponse() {
    }

    public AuthResponse(String status, String message, Set<String> role, String token) {
        this.message = message;
        this.status = status;
        this.role = role;
        this.token = token;
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

    public Set<String> getRole() {
        return role;
    }
    public void setRole(Set<String> role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
