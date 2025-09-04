package com.groupthree.shopsphere.controller;
import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.repository.ProductRepository;
import org.springframework.data.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vendor")
public class VendorController {
    private final ProductRepository productRepo;

    public VendorController( ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    @GetMapping("/{id}/products")
    public Iterable<Product> findById(@PathVariable Long id) {
        return productRepo.findByVendorId(id);
    }

    @PostMapping("/{id}/add")
    public Product addProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setVendorId(id);
        return productRepo.save(product);
    }

    @PutMapping("/{id}/products/{productId}")
    public Product updateProduct(@PathVariable Long id, @PathVariable Long productId, @RequestBody Product product) {
        Product prod = productRepo.findById(productId).orElseThrow();
        if (!prod.getVendorId().equals(id)) {
            throw new RuntimeException();
        }
        prod.setPrice(product.getPrice());
        prod.setCategory(product.getCategory());
        prod.setRating(product.getRating());
        prod.setInStock(product.getInStock());
        prod.setReviewCount(product.getReviewCount());
        return productRepo.save(prod);

    }
    @DeleteMapping("/{id}/delete/{productId}")
    public void deleteProduct(@PathVariable Long id, @PathVariable Long productId) {
        Product prod = productRepo.findById(productId).orElseThrow();
        if (!prod.getVendorId().equals(id)) {
            throw new RuntimeException();
        }
        productRepo.delete(prod);
    }
}
