package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.requests.LoginRequest;
import com.groupthree.shopsphere.dto.requests.RegisterRequest;
import com.groupthree.shopsphere.dto.responses.AuthResponse;
import com.groupthree.shopsphere.dto.responses.UserResponse;
import com.groupthree.shopsphere.security.JwtUtil;

import com.groupthree.shopsphere.security.TokenBlacklistService;
import jakarta.validation.Valid;

import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = {"/auth/", "/auth"})
public class AuthController {
    private final UserRepository repo;
    private final TokenBlacklistService tokenBlacklistService;
    BCryptPasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthController(UserRepository repo, JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.tokenBlacklistService = tokenBlacklistService;
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = repo.findByEmail(email);
        return user.getId();
    }

    @PostMapping(value = {"/register/", "/register"})
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
        try {
            if (repo.findByEmail(request.getEmail()) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthResponse("error", "Email already exists"));
            }
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            // Grant CUSTOMER authority
            Set<String> roles = new HashSet<>();
            roles.add("CUSTOMER");
            user.setRole(roles);
            repo.save(user);
            return ResponseEntity.ok(new AuthResponse("success", "Registered", roles, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("error", "Invalid token"));
        }
    }

    @PostMapping(value = {"/register/vendor/", "/register/vendor"})
    public ResponseEntity<AuthResponse> vendorRegister(@Valid @RequestBody RegisterRequest request, @RequestHeader(value="Authorization",required = false) String token) {
        try {
            User existing = repo.findByEmail(request.getEmail());
            if (existing != null) {
                if (existing.getRole().contains("VENDOR")) { // Already a vendor
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new AuthResponse("error", "Vendor already exists", existing.getRole(), null, null));
                }
                Set<String> newRole=existing.getRole();
                newRole.add("VENDOR");
                existing.setRole(newRole);
                repo.save(existing);
                tokenBlacklistService.blacklistToken(token);
                return ResponseEntity.ok(new AuthResponse("success", "Updated customer to vendor. Login again.", existing.getRole(), null, null));
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
            tokenBlacklistService.blacklistToken(token);
            return ResponseEntity.ok(new AuthResponse("success", "Registered", roles, null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("error", "Invalid token"));
        }
    }

    @PostMapping(value = {"/login/", "/login"})
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = repo.findByEmail(request.getEmail());
            if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("error", "Invalid email or password"));
            }
            String accessToken = jwtUtil.generateAccessToken(user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            return ResponseEntity.ok(new AuthResponse("success", "Logged in", user.getRole(), accessToken, refreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("error", "Invalid token"));
        }
    }

    @GetMapping(value = {"/current/", "/current"})
    public ResponseEntity<UserResponse> getCurrentUser() {
        try {
            Long userId = getUserIdFromToken();
            User user = repo.findById(userId).orElseThrow();
            return ResponseEntity.ok(new UserResponse(
                    "success",
                    "User obtained successfully",
                    userId,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail(),
                    user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new UserResponse("error", e.getMessage()));
        }
    }

    @GetMapping(value = {"/logout/", "/logout"})
    public ResponseEntity<AuthResponse> logout(@RequestHeader("Authorization") String accessToken, @RequestHeader("Refresh-Token") String refreshToken) {
        try {
            tokenBlacklistService.blacklistToken(accessToken);
            tokenBlacklistService.blacklistToken(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("error", "Invalid token"));
        }
        return ResponseEntity.ok(new AuthResponse("success", "Logged out"));
    }

    // Refresh tokens
    @PostMapping(value = {"/refresh/", "/refresh"})
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        try {
            if (tokenBlacklistService.isBlacklisted(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("error", "Refresh token has been blacklisted"));
            }

            String email = jwtUtil.extractEmail(refreshToken);
            User user = repo.findByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new AuthResponse("error", "User not found"));
            }

            String newAccessToken = jwtUtil.generateAccessToken(email);

            return ResponseEntity.ok(new AuthResponse("success", "Refreshed", user.getRole(), newAccessToken, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse("error", "Invalid refresh token"));
        }
    }
}