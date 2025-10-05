package com.groupthree.shopsphere.dto.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Email;

public class LoginRequest {
    @Email
    private String email;
    
    @NotEmpty
    private String password;

    // Getters and setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
