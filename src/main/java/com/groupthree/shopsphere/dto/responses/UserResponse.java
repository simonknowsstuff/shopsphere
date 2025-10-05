package com.groupthree.shopsphere.dto.responses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class UserResponse {
    @NotNull
    private String status;
    @NotEmpty
    private String message;
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<String> role;

    public UserResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public UserResponse(String status, String message, Long id, String firstName, String lastName, String email, Set<String> role) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
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

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRole() {
        return role;
    }
    public void setRole(Set<String> role) {
        this.role = role;
    }
}
