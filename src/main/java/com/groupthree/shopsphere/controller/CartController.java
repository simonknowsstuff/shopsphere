package com.groupthree.shopsphere.controller;
import com.groupthree.shopsphere.models.Cart;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.CartRepository;
import com.groupthree.shopsphere.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
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
        Long id = getUserIdFromToken();
        return repo.findByUserId(id);
    }

    @PostMapping
    public Cart save(@RequestBody Cart cart) {
        return repo.save(cart);
    }

    @PutMapping("/")
    public Cart updateQuantity(@RequestBody Cart cart){
        Long id = getUserIdFromToken();
        Cart existing=repo.findByUserIdAndProductId(id,cart.getProductId()).orElseThrow();
        existing.setQuantity(cart.getQuantity());
        return repo.save(existing);
    }

    @DeleteMapping("/")
    public void delete(@RequestBody Map<String,Long> body) {
        Long id = getUserIdFromToken();
        Long productId = body.get("productId");
        Cart cur=repo.findByUserIdAndProductId(id,productId).orElseThrow();
        repo.delete(cur);
    }
}
