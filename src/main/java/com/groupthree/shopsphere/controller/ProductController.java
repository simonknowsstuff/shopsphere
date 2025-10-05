package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.responses.ProductResponse;
import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.repository.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = {"/products/", "/products"})
public class ProductController {
    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    private List<ProductResponse> productToResponse(Iterable<Product> products) {
        try {
            List<ProductResponse> productResponses = new ArrayList<>();
            for (Product product : products) {
                ProductResponse response = new ProductResponse(
                        "success",
                        "Product obtained successfully",
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
                productResponses.add(response);
            }
            return productResponses;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = {"/", ""})
    public ResponseEntity<Iterable<ProductResponse>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        try {
            Iterable<Product> products;
            if (category != null) {
                products = repo.findByCategoryIgnoreCase(category);
            } else if (search != null) {
                products = repo.findByNameContainingIgnoreCase(search);
            } else {
                products = repo.findAll();
            }
            
            List<ProductResponse> response = productToResponse(products);
            if (response.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singleton(new ProductResponse(
                        "not_found",
                        "No products found with given criteria"
                    )));
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singleton(new ProductResponse(
                    "error",
                    "An error occurred while fetching products: " + e.getMessage()
                )));
        }
    }

    @GetMapping(value = {"/{id}/", "/{id}"})
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        try {
            Product product = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
            
            ProductResponse response = new ProductResponse(
                    "success",
                    "Product obtained successfully",
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
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            HttpStatus status = e.getMessage().contains("not found") ? 
                HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            
            return ResponseEntity.status(status)
                .body(new ProductResponse(
                    "error",
                    e.getMessage()
                ));
        }
    }
}