package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping
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

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return repo.save(product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        repo.deleteById(id);
    }
}