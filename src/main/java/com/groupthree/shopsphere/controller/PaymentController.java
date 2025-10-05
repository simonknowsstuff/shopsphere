package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.responses.PaymentResponse;
import com.groupthree.shopsphere.models.Order;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.OrderRepository;
import com.groupthree.shopsphere.repository.UserRepository;

import com.groupthree.shopsphere.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = {"/payment/", "/payment"})
public class PaymentController {
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final JwtUtil jwtUtil;

    public PaymentController(UserRepository userRepo, OrderRepository orderRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.jwtUtil = jwtUtil;
    }

    private Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepo.findByEmail(email);
        return user.getId();
    }

    @PostMapping(value = {"/pay/{orderId}/", "/pay/{orderId}"})
    public ResponseEntity<PaymentResponse> pay(@PathVariable Long orderId) {
        try {
            Long userId = getUserIdFromToken();
            Optional<Order> order = orderRepo.findByIdAndUserId(orderId, userId);
        
            if (order.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new PaymentResponse("error", "Order not found or does not belong to current user")
                );
            }

            Order currentOrder = order.get();
            if (!currentOrder.getStatus().equals("PENDING")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new PaymentResponse("error", "Order is not in PENDING status")
                );
            }
        
            long PAYMENT_EXPIRATION = 300000; // 5 minutes for payment
            String paymentToken = jwtUtil.generateToken(orderId.toString(), PAYMENT_EXPIRATION);
        
            // Update order status to PAYMENT_PENDING
            currentOrder.setStatus("PAYMENT_PENDING");
            orderRepo.save(currentOrder);
        
            return ResponseEntity.ok(new PaymentResponse(
                "success",
                "Payment token generated successfully",
                paymentToken
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new PaymentResponse("error", e.getMessage())
            );
        }
    }

    @PostMapping(value = {"/confirm/", "/confirm"})
    public ResponseEntity<PaymentResponse> confirm(@RequestHeader(value = "Payment-Token") String paymentToken) {
        try {
            if (!jwtUtil.validateToken(paymentToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new PaymentResponse("error", "Invalid or expired payment token")
                );
            }

            String orderIdStr = jwtUtil.extractSubject(paymentToken);
            Long orderId = Long.parseLong(orderIdStr);
            Long userId = getUserIdFromToken();
        
        Optional<Order> order = orderRepo.findByIdAndUserId(orderId, userId);
            if (order.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new PaymentResponse("error", "Order not found or does not belong to current user")
                );
            }

            Order confirmedOrder = order.get();
            if (!confirmedOrder.getStatus().equals("PAYMENT_PENDING")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new PaymentResponse("error", "Order is not in PAYMENT_PENDING status")
                );
            }

            confirmedOrder.setStatus("PAYMENT_COMPLETED");
            orderRepo.save(confirmedOrder);

            return ResponseEntity.ok(new PaymentResponse(
                "success",
                "Payment confirmed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new PaymentResponse("error", e.getMessage())
            );
        }
    }
}