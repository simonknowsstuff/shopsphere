package com.groupthree.shopsphere.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("REVIEWS")
public class Review {
    @Id
    private Long id;

    private Long userId;
    private Long productId;

    @NotNull
    @Min(1)
    @Max(5)
    private double rating;

    @NotEmpty
    @Size(min = 15, max = 500)
    private String review;

    public Review() {
        this.rating = 0;
    }

    public Review(Long userId, Long productId, double rating, String review) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.review = review;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }
}
