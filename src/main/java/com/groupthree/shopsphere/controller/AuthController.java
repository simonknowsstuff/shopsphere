package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.requests.LoginRequest;
import com.groupthree.shopsphere.dto.requests.RegisterRequest;
import com.groupthree.shopsphere.dto.responses.AuthResponse;
import com.groupthree.shopsphere.security.JwtUtil;

import jakarta.validation.Valid;

import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.UserRepository;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = {"/auth/", "/auth"})
public class AuthController {
    private final UserRepository repo;
    BCryptPasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthController(UserRepository repo, JwtUtil jwtUtil) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping(value = {"/register/", "/register"})
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){
        if (repo.findByEmail(request.getEmail()) != null){
            return new AuthResponse("error","email already exists",null,null);
        }
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Grant CUSTOMER authority
        Set<String> roles=new HashSet<>();
        roles.add("CUSTOMER");
        user.setRole(roles);
        repo.save(user);
        return new AuthResponse("success","Registered",roles,null);
    }

    @PostMapping(value = {"/register/vendor/", "/register/vendor"})
    public AuthResponse vendorRegister(@Valid @RequestBody RegisterRequest request){
        User existing = repo.findByEmail(request.getEmail());
        if (existing != null) {
            if (existing.getRole().contains("VENDOR")) { // Already a vendor
                return new AuthResponse("error", "Vendor already exists", existing.getRole(), null);
            }
            existing.getRole().add("VENDOR"); // Or just update the customer to vendor
            repo.save(existing);
            return new AuthResponse("success", "Updated customer to vendor", existing.getRole(), null);
        }
        // Else, create a completely new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<String> roles = new HashSet<>();
        roles.add("CUSTOMER");
        roles.add("VENDOR");
        user.setRole(roles);
        repo.save(user);
        return new AuthResponse("success", "Registered", roles, null);
    }

    @PostMapping(value = {"/login", "/login/"})
    public AuthResponse login(@Valid @RequestBody LoginRequest request){
        User user = repo.findByEmail(request.getEmail());
        if (user == null||!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            return new AuthResponse("error","Invalid email or password",null,null);
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse("success","Logged in",user.getRole(),token);
    }

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<String> handleDbActionExecutionException(DbActionExecutionException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}