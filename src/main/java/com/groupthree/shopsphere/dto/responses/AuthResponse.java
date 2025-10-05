package com.groupthree.shopsphere.dto.responses;
import java.util.Set;
public class AuthResponse {
    private String status;
    private String message;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String firstName;
    private String lastName;
    private Set<String> role;

    public AuthResponse(String status, String message) {
        this.message = message;
        this.status = status;
    }

    public AuthResponse(String status, String message, Set<String> role, String accessToken, String refreshToken) {
        this.message = message;
        this.status = status;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthResponse(String status, String message, Set<String> role, String accessToken, String refreshToken, String email, String firstName, String lastName) {
        this.message = message;
        this.status = status;
        this.role = role;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and setters
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
