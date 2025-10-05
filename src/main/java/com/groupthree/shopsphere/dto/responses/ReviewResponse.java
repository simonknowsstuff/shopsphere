package com.groupthree.shopsphere.dto.responses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ReviewResponse {
    @NotNull
    private String status;
    @NotEmpty
    private String message;

    private Long id;
    private Long userId;
    private Long productId;
    private String review;

    public ReviewResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ReviewResponse(String status, String message, Long id, Long userId, Long productId, String review) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.review = review;
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

    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }
}
