package com.groupthree.shopsphere.controller;
import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.repository.ProductRepository;
import com.groupthree.shopsphere.repository.UserRepository;
import com.groupthree.shopsphere.models.User;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendor")
public class VendorController {
    private final ProductRepository productRepo;
    private final UserRepository userRepository;

    public VendorController( ProductRepository productRepo, UserRepository userRepository) {
        this.productRepo = productRepo;
        this.userRepository = userRepository;
    }

    public Long getVendorIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @GetMapping("/products")
    public Iterable<Product> findById() {
        Long vendorId = getVendorIdFromToken();
        return productRepo.findByVendorId(vendorId);
    }

    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product) {
        Long vendorId = getVendorIdFromToken();
        product.setVendorId(vendorId);
        return productRepo.save(product);
    }

    @PutMapping("/products/{productId}")
    public Product updateProduct(@PathVariable Long productId, @RequestBody Product product) {
        Long vendorId = getVendorIdFromToken();
        Product prod = productRepo.findById(productId).orElseThrow();
        if (!prod.getVendorId().equals(vendorId)) {
            throw new RuntimeException();
        }
        prod.setPrice(product.getPrice());
        prod.setCategory(product.getCategory());
        prod.setRating(product.getRating());
        prod.setInStock(product.getInStock());
        prod.setReviewCount(product.getReviewCount());
        return productRepo.save(prod);

    }
    @DeleteMapping("/delete/{productId}")
    public void deleteProduct(@PathVariable Long productId) {
        Long vendorId = getVendorIdFromToken();
        Product prod = productRepo.findById(productId).orElseThrow();
        if (!prod.getVendorId().equals(vendorId)) {
            throw new RuntimeException();
        }
        productRepo.delete(prod);
    }
}
