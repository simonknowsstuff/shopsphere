package com.groupthree.shopsphere.models;
// Table for users with required fields, not implemented yet.
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Table("users")
public class User {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User() {} // No args constructor

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
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
        // Only hash if password is not hashed:
        if (!password.startsWith("$2a$")) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            this.password = passwordEncoder.encode(password);
        } else {
            this.password = password;
        }
    }
    public String getPassword() {
        return password;
    }
}
