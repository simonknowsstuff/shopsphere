package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Cart;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.CartRepository;
import com.groupthree.shopsphere.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final UserRepository userRepository;
    private final CartRepository repo;

    public CartController(CartRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @GetMapping("/")
    public Iterable<Cart> findAll() {
        Long userId = getUserIdFromToken();
        return repo.findByUserId(userId);
    }

    @PostMapping("/")
    public Cart save(@Valid @RequestBody Cart cart) {
        Long userId = getUserIdFromToken();
        cart.setUserId(userId);
        return repo.save(cart);
    }

    @PutMapping("/")
    public Cart updateQuantity(@Valid @RequestBody Cart cart){
        Long userId = getUserIdFromToken();
        Cart existing = repo.findByUserIdAndProductId(userId, cart.getProductId()).orElseThrow();
        existing.setQuantity(cart.getQuantity());
        return repo.save(existing);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> delete(@RequestBody Map<String,Long> body) {
        Long userId = getUserIdFromToken();
        Long productId = body.get("productId");
        Cart cur = repo.findByUserIdAndProductId(userId, productId).orElseThrow();
        repo.delete(cur);
        return ResponseEntity.status(HttpStatus.OK).body("Item deleted from cart");
    }
}
