package com.groupthree.shopsphere.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;

import java.util.HashSet;
import java.util.Set;

@Table("USERS")
public class User {
    @Id
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
    
    @NotNull
    private String email;
    
    @NotNull
    private String password;
    
    @NotNull
    private String role;

    public User() {
        this.role = "CUSTOMER";
    }

    public User(Long id, String firstName, String lastName, String email, String password, String role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }

    public void setPassword(String password) {
            this.password = password;
    }
    public String getPassword() {
        return password;
    }

    public void setRole(Set<String> role) {
        this.role = String.join(",", role);
    }
    public Set<String> getRole() {
        if(role==null||role.isEmpty()){
            return new HashSet<>();
        }
        Set<String> roles=new HashSet<>();
        for(String s:role.split(",")){
            roles.add(s.trim().toUpperCase());
        }
        return roles;
    }
}
