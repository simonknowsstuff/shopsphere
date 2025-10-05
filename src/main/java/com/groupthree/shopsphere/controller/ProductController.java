package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.responses.ProductResponse;
import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.repository.ProductRepository;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
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
    public Iterable<ProductResponse> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        try {
            if (category != null) {
                return productToResponse(repo.findByCategoryIgnoreCase(category));
            } else if (search != null) {
                return productToResponse(repo.findByNameContainingIgnoreCase(search));
            } else {
                return productToResponse(repo.findAll());
            }
        } catch (Exception e) {
            return Collections.singleton(new ProductResponse(
                    "error",
                    e.getMessage()
            ));
        }
    }

    @GetMapping(value = {"/{id}/", "/{id}"})
    public ProductResponse getProduct(@PathVariable Long id) {
        try {
            Product product = repo.findById(id).orElseThrow();
            return new ProductResponse(
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
        } catch (Exception e) {
            return new ProductResponse("error", e.getMessage());
        }
    }
}