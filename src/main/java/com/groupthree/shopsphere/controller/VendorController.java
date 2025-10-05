package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.responses.ProductResponse;
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
    public ProductResponse addProduct(@Valid @RequestBody Product product) {
        try {
            Long vendorId = getVendorIdFromToken();
            product.setVendorId(vendorId);
            productRepo.save(product);
            return new ProductResponse(
                    "success",
                    "Product added successfully",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getOriginalPrice(),
                    product.getDescription(),
                    product.getCategory(),
                    product.getImage(),
                    product.getRating(),
                    product.getReviewCount(),
                    product.getInStock(),
                    product.getFeatures(),
                    product.getVendorId()
            );
        } catch (Exception e) {
            return new ProductResponse("error: ", e.getMessage());
        }
    }

    @PutMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ProductResponse updateProduct(@PathVariable Long productId, @Valid @RequestBody Product product) {
        try {
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
            productRepo.save(prod);
            return new ProductResponse(
                    "success",
                    "Product updated successfully",
                    prod.getId(),
                    prod.getName(),
                    prod.getPrice(),
                    prod.getOriginalPrice(),
                    prod.getDescription(),
                    prod.getCategory(),
                    prod.getImage(),
                    prod.getRating(),
                    prod.getReviewCount(),
                    prod.getInStock(),
                    prod.getFeatures(),
                    prod.getVendorId()
            );
        } catch (Exception e) {
            return new ProductResponse("error", e.getMessage());
        }
    }
    
    @DeleteMapping(value = {"/delete/{productId}/", "/delete/{productId}"})
    public ProductResponse deleteProduct(@PathVariable Long productId) {
        try {
            Long vendorId = getVendorIdFromToken();
            Product prod = productRepo.findById(productId).orElseThrow();
            if (!prod.getVendorId().equals(vendorId)) {
                throw new RuntimeException();
            }
            productRepo.delete(prod);
        } catch (Exception e) {
            return new ProductResponse("error", e.getMessage());
        }
        return new ProductResponse("success", "Product deleted successfully");
    }
}
