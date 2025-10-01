package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.repository.ProductRepository;
import com.groupthree.shopsphere.repository.UserRepository;
import com.groupthree.shopsphere.models.User;

import jakarta.validation.Valid;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/vendor/", "/vendor"})
public class VendorController {
    private final ProductRepository productRepo;
    private final UserRepository userRepository;

    public VendorController(ProductRepository productRepo, UserRepository userRepository) {
        this.productRepo = productRepo;
        this.userRepository = userRepository;
    }

    public Long getVendorIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        // Ensure user has VENDOR authority
        if (!user.getRole().contains("VENDOR")) {
            throw new RuntimeException("User does not have VENDOR authority");
        }
        return user.getId();
    }

    @GetMapping(value = {"/products/", "/products"})
    public Iterable<Product> findById() {
        Long vendorId = getVendorIdFromToken();
        return productRepo.findByVendorId(vendorId);
    }

    @PostMapping(value = {"/add/", "/add"})
    public Product addProduct(@Valid @RequestBody Product product) {
        Long vendorId = getVendorIdFromToken();
        product.setVendorId(vendorId);
        return productRepo.save(product);
    }

    @PutMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public Product updateProduct(@PathVariable Long productId, @Valid @RequestBody Product product) {
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
    
    @DeleteMapping(value = {"/delete/{productId}/", "/delete/{productId}"})
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        Long vendorId = getVendorIdFromToken();
        Product prod = productRepo.findById(productId).orElseThrow();
        if (!prod.getVendorId().equals(vendorId)) {
            throw new RuntimeException();
        }
        productRepo.delete(prod);
        return ResponseEntity.status(200).body("Product deleted");
    }

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<String> handleDbActionExecutionException(DbActionExecutionException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
