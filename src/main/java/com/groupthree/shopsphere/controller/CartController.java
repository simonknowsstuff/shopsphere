package com.groupthree.shopsphere.controller;
import com.groupthree.shopsphere.models.Cart;
import com.groupthree.shopsphere.repository.CartRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartRepository repo;

    public CartController(CartRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/{id}")
    public Iterable<Cart> findAll(@PathVariable Long id) {
        return repo.findByUserId(id);
    }

    @PostMapping
    public Cart save(@RequestBody Cart cart) {
        return repo.save(cart);
    }

    @PutMapping("/{id}")
    public Cart updateQuantity(@PathVariable Long id, @RequestBody Cart cart){
        Cart existing=repo.findById(id).orElseThrow();
        existing.setQuantity(cart.getQuantity());
        return repo.save(existing);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
