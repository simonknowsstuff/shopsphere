package com.groupthree.shopsphere.dto.responses;

import com.groupthree.shopsphere.models.Review;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReviewResponse {
    @NotNull
    private String status;
    @NotEmpty
    private String message;

    private Long id;
    private Long userId;
    private Long productId;
    private double rating;
    private String review;

    public ReviewResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public ReviewResponse(String status, String message, Long id, Long userId, Long productId, double rating, String review) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.review = review;
    }

    public static ReviewResponse fromReview(Review savedReview, String message) {
        return new ReviewResponse(
                "success",
                message,
                savedReview.getId(),
                savedReview.getUserId(),
                savedReview.getProductId(),
                savedReview.getRating(),
                savedReview.getReview()
        );
    }

    public static List<ReviewResponse> getReviewResponses(Iterable<Review> reviews) {
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for (Review review : reviews) {
            ReviewResponse reviewResponse = new ReviewResponse(
                    "success",
                    "Review obtained successfully",
                    review.getId(),
                    review.getUserId(),
                    review.getProductId(),
                    review.getRating(),
                    review.getReview()
            );
            reviewResponses.add(reviewResponse);
        }
        return reviewResponses;
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
