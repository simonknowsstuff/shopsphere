package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Cart;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.CartRepository;
import com.groupthree.shopsphere.repository.UserRepository;
import com.groupthree.shopsphere.dto.responses.CartResponse;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping(value = {"/cart/", "/cart"})
public class CartController {

    private final UserRepository userRepository;
    private final CartRepository repo;

    public CartController(CartRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    private void validateCart(Cart cart) {
        if (cart.getQuantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than 0");
        }
        if (cart.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be provided");
        }
        if (cart.getProductId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID must be provided");
        }
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @GetMapping(value = {"/", ""})
    public ResponseEntity<Iterable<CartResponse>> findAll() {
        try {
            Long userId = getUserIdFromToken();
            return ResponseEntity.ok(CartResponse.getCartResponses(repo.findByUserId(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singleton(new CartResponse(
                    "error",
                    e.getMessage()
            )));
        }
    }

    @PostMapping(value = {"/", ""})
    public ResponseEntity<CartResponse> save(@Valid @RequestBody Cart cart) {
        try {
            Long userId = getUserIdFromToken();
            Optional<Cart> existingOpt = repo.findByUserIdAndProductId(userId, cart.getProductId());
            if (existingOpt.isPresent()) {
                Cart existing = existingOpt.get();
                existing.setQuantity(existing.getQuantity() + 1);
                repo.save(existing);
                return ResponseEntity.ok(CartResponse.fromCart("Cart updated successfully", existing));
            } else {
                cart.setUserId(userId);
                validateCart(cart);
                repo.save(cart);
                return ResponseEntity.status(HttpStatus.CREATED).body(CartResponse.fromCart("Cart added successfully", cart));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartResponse(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    @PutMapping(value = {"/", ""})
    public ResponseEntity<CartResponse> updateQuantity(@Valid @RequestBody Cart cart){
        try {
            Long userId = getUserIdFromToken();
            Cart existing = repo.findByUserIdAndProductId(userId, cart.getProductId()).orElseThrow();
            existing.setQuantity(cart.getQuantity());
            repo.save(existing);
            return ResponseEntity.ok(CartResponse.fromCart("Cart quantity updated successfully", existing));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartResponse(
                    "error",
                    e.getMessage()
            ));
        }
    }

    @DeleteMapping(value = {"/{productId}"})
    public ResponseEntity<CartResponse> delete(@PathVariable Long productId) {
        try {
            Long userId = getUserIdFromToken();

            Cart cur = repo.findByUserIdAndProductId(userId, productId).orElseThrow();
            repo.delete(cur);
            return ResponseEntity.ok(new CartResponse("success", "Cart deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartResponse(
                            "error",
                            e.getMessage()
                    ));
        }
    }

    @DeleteMapping(value={"/",""})
    public ResponseEntity<CartResponse> clearUser() {
        try {
            Long userId = getUserIdFromToken();
            repo.deleteAllByUserId(userId);
            return ResponseEntity.ok(new CartResponse("success", "Cart removed from user"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartResponse(
                            "error",
                            e.getMessage()
                    ));
        }
    }
}
