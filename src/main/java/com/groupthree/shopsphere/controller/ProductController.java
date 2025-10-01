package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.repository.ProductRepository;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"/products/", "/products"})
public class ProductController {
    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping(value = {"/", ""})
    public Iterable<Product> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        if (category != null) {
            return repo.findByCategoryIgnoreCase(category);
        } else if (search != null) {
            return repo.findByNameContainingIgnoreCase(search);
        } else {
            return repo.findAll();
        }
    }

    @GetMapping(value = {"/{id}/", "/{id}"})
    public Product getProduct(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<String> handleDbActionExecutionException(DbActionExecutionException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}