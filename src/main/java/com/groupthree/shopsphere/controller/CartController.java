package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Cart;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.CartRepository;
import com.groupthree.shopsphere.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
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
    public Iterable<Cart> findAll() {
        Long userId = getUserIdFromToken();
        return repo.findByUserId(userId);
    }

    @PostMapping(value = {"/", ""})
    public Cart save(@Valid @RequestBody Cart cart) {
        Long userId = getUserIdFromToken();
        Optional<Cart> existingOpt = repo.findByUserIdAndProductId(userId,cart.getProductId());
        if(existingOpt.isPresent()){
            Cart existing=existingOpt.get();
            existing.setQuantity(existing.getQuantity()+1);
            return repo.save(existing);
        }
        else{
        cart.setUserId(userId);
        validateCart(cart);
        return repo.save(cart);}
    }

    @PutMapping(value = {"/", ""})
    public Cart updateQuantity(@Valid @RequestBody Cart cart){
        Long userId = getUserIdFromToken();
        Cart existing = repo.findByUserIdAndProductId(userId, cart.getProductId()).orElseThrow();
        existing.setQuantity(cart.getQuantity());
        return repo.save(existing);
    }

    @DeleteMapping(value = {"/{productId}"})
    public ResponseEntity<String> delete(@PathVariable Long productId) {
        Long userId = getUserIdFromToken();

        Cart cur = repo.findByUserIdAndProductId(userId, productId).orElseThrow();
        repo.delete(cur);
        return ResponseEntity.status(HttpStatus.OK).body("Item deleted from cart");
    }

    @DeleteMapping(value={"/",""})
    public ResponseEntity<String> clearUser() {
        Long userId = getUserIdFromToken();
        repo.deleteAllByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body("Cart cleared");
    }

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<String> handleDbActionExecutionException(DbActionExecutionException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
