package com.groupthree.shopsphere.dto.responses;
import java.util.Set;
public class AuthResponse {
    private String status;
    private String message;
    private String accessToken;
    private String refreshToken;
    private Set<String> role;

    public AuthResponse() {
    }

    public AuthResponse(String status, String message, Set<String> role, String accessToken, String refreshToken) {
        this.message = message;
        this.status = status;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
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

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
