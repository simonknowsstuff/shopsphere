package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.responses.ProductResponse;
import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.repository.ProductRepository;
import com.groupthree.shopsphere.repository.UserRepository;
import com.groupthree.shopsphere.models.User;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;


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
    public ResponseEntity<Iterable<ProductResponse>> findById() {
        try {
            Long vendorId = getVendorIdFromToken();
            return ResponseEntity.ok(ProductResponse.getProductResponses(productRepo.findByVendorId(vendorId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping(value = {"/add/", "/add"})
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody Product product) {
        try {
            Long vendorId = getVendorIdFromToken();
            product.setVendorId(vendorId);
            productRepo.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.fromProduct(product, "Product added successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ProductResponse("error: ", e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "No file provided"));
            }

            // Save to static folder
            String uploadsDir = new File("uploads").getAbsolutePath(); // adjust to your static folder
            File dir = new File(uploadsDir);
            if (!dir.exists()) dir.mkdirs();

            String filename = System.currentTimeMillis() + "_" + Paths.get(file.getOriginalFilename()).getFileName();
            File dest = new File(dir, filename);
            file.transferTo(dest);

            // Return filename (frontend can prepend /uploads/ if needed)
            return ResponseEntity.ok(Map.of("filename", filename));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @Valid @RequestBody Product product) {
        try {
            Long vendorId = getVendorIdFromToken();
            Product existingProduct = productRepo.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            
            if (!existingProduct.getVendorId().equals(vendorId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this product");
            }
        
            // Update all relevant fields
            existingProduct.setName(product.getName());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setOriginalPrice(product.getOriginalPrice());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setCategory(product.getCategory());
            existingProduct.setImage(product.getImage());
            existingProduct.setRating(product.getRating());
            existingProduct.setInStock(product.getInStock());
            existingProduct.setReviewCount(product.getReviewCount());
            existingProduct.setFeatures(product.getFeatures());
        
            Product savedProduct = productRepo.save(existingProduct);
            return ResponseEntity.ok(ProductResponse.fromProduct(savedProduct, "Product updated successfully"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update product");
        }
    }
    
    @DeleteMapping(value = {"/delete/{productId}/", "/delete/{productId}"})
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable Long productId) {
        try {
            Long vendorId = getVendorIdFromToken();
            Product prod = productRepo.findById(productId).orElseThrow();
            if (!prod.getVendorId().equals(vendorId)) {
                throw new RuntimeException();
            }
            productRepo.delete(prod);
            return ResponseEntity.ok(new ProductResponse("success", "Product deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ProductResponse("error", e.getMessage()));
        }
    }
}