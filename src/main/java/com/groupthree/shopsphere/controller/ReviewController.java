package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Review;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.ReviewRepository;

import com.groupthree.shopsphere.repository.UserRepository;
import jakarta.validation.Valid;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class ReviewController {
    private final ReviewRepository repo;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @GetMapping("/products/{productId}/reviews")
    public Iterable<Review> getReviews(@PathVariable Long productId) {
        return repo.findByProductId(productId);
    }

    @PostMapping("/products/{productId}/reviews")
    public Review addReview(@Valid @RequestBody Review review, @PathVariable Long productId) {
        Long userId = getUserIdFromToken();
        review.setUserId(userId);
        review.setProductId(productId);
        return repo.save(review);
    }

    @GetMapping("/users/{userId}/reviews")
    public Iterable<Review> getReviewsByUser(@PathVariable Long userId) {
        return repo.findByUserId(userId);
    }

    @DeleteMapping("/users/{userId}/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long userId, @PathVariable Long reviewId) {
        Long tokenUserId = getUserIdFromToken();
        Review review = repo.findById(reviewId).orElseThrow();
        if (!review.getUserId().equals(tokenUserId) && !userId.equals(tokenUserId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User does not have permission to delete review"
            );
        }
        repo.delete(review);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("Review deleted");
    }
}
