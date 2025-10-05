package com.groupthree.shopsphere.dto.responses;

import com.groupthree.shopsphere.models.Product;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProductResponse {
    @NotNull
    private String status;
    @NotEmpty
    private String message;

    private Long id;
    private String name;
    private double price;
    private double originalPrice;
    private String description;
    private String category;
    private String image;
    private double rating;
    private int reviewCount;
    private boolean inStock;
    private List<String> features;
    private Long vendorId;

    public ProductResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ProductResponse(String status, String message, Long id, String name, double price, double originalPrice, String description, String category, String image, double rating, int reviewCount, boolean inStock, List<String> features, Long vendorId) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.name = name;
        this.price = price;
        this.originalPrice = originalPrice;
        this.description = description;
        this.category = category;
        this.image = image;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.inStock = inStock;
        this.features = features;
        this.vendorId = vendorId;
    }

    public static ProductResponse fromProduct(Product savedProduct, String message) {
        return new ProductResponse(
                "success",
                message,
                savedProduct.getId(),
                savedProduct.getName(),
                savedProduct.getPrice(),
                savedProduct.getOriginalPrice(),
                savedProduct.getDescription(),
                savedProduct.getCategory(),
                savedProduct.getImage(),
                savedProduct.getRating(),
                savedProduct.getReviewCount(),
                savedProduct.getInStock(),
                savedProduct.getFeatures(),
                savedProduct.getVendorId()
        );
    }

    public static List<ProductResponse> getProductResponses(Iterable<Product> products) {
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

    // Getters and setters
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }
    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean getInStock() {
        return inStock;
    }
    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public List<String> getFeatures() {
        return features;
    }
    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
}
