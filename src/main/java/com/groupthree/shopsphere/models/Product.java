package com.groupthree.shopsphere.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Arrays;
import java.util.List;


@Table("products")
public class Product {
    @Id
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
    private String features;

    public Product() {}

    public Product(
     Long id,
     String name,
     double price,
     double originalPrice,
     String description,
     String category,
     String image,
     double rating,
     int reviewCount,
     boolean inStock,
     List<String> features){
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
        this.features = String.join(",", features);
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

    public boolean isInStock() {
        return inStock;
    }
    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public List<String> getFeatures() {
        return Arrays.asList(this.features.split(","));
    }
    public void setFeatures(List<String> features) {
        this.features = String.join(",", features);
    }
}