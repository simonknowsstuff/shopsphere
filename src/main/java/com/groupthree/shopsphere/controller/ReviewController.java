package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.responses.ReviewResponse;
import com.groupthree.shopsphere.models.Review;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.ReviewRepository;

import com.groupthree.shopsphere.repository.UserRepository;

import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = {"/reviews/", "/reviews"})
public class ReviewController {
    private final ReviewRepository repo;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    private void validateReview(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }
        if (review.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must be provided");
        }
        if (review.getProductId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product ID must be provided");
        }
        if (review.getReview() == null || review.getReview().length() < 15 || review.getReview().length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review text must be between 15 and 500 characters");
        }
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @GetMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ResponseEntity<Iterable<ReviewResponse>> getReviews(@PathVariable Long productId) {
        try {
            return ResponseEntity.ok(ReviewResponse.getReviewResponses(repo.findByProductId(productId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singleton(new ReviewResponse(
                    "error",
                    e.getMessage()
            )));
        }
    }

    @PostMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ResponseEntity<ReviewResponse> addReview(@Valid @RequestBody Review review, @PathVariable Long productId) {
        try {
            Long userId = getUserIdFromToken();
            // Check if user already has a review for this product
            if (repo.findByUserIdAndProductId(userId, productId).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ReviewResponse("error", "Product already reviewed"));
            }
            review.setUserId(userId);
            review.setProductId(productId);
            validateReview(review);
            repo.save(review);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReviewResponse.fromReview(review, "Review added successfully"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(new ReviewResponse("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReviewResponse("error", e.getMessage()));
        }
    }

    @PutMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ResponseEntity<ReviewResponse> updateReview(@Valid @RequestBody Review review, @PathVariable Long productId) {
        try {
            Long userId = getUserIdFromToken();
            Review existing = repo.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "Review not found. Create a review first using POST."
                ));
            existing.setReview(review.getReview());
            existing.setRating(review.getRating());
            validateReview(existing);
            repo.save(existing);
            return ResponseEntity.ok(ReviewResponse.fromReview(existing, "Review updated successfully"));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(new ReviewResponse("error", e.getReason()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReviewResponse("error", e.getMessage()));
        }
    }

    @DeleteMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ResponseEntity<ReviewResponse> deleteReview(@PathVariable Long productId) {
        try {
            Long userId = getUserIdFromToken();
            Review review = repo.findByUserIdAndProductId(userId, productId).orElseThrow();
            repo.delete(review);
            return ResponseEntity.ok(new ReviewResponse("success", "Review deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ReviewResponse("error", e.getMessage())
            );
        }
    }

    @GetMapping(value = {"/user/{userId}/", "/user/{userId}"})
    public ResponseEntity<Iterable<ReviewResponse>> getReviewsByUser(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(ReviewResponse.getReviewResponses(repo.findByUserId(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singleton(new ReviewResponse("error", e.getMessage()))
            );
        }
    }

    @GetMapping(value={"/user","/user/"})
    public ResponseEntity<Iterable<ReviewResponse>> getOurReviews() {
        try {
            Long userId = getUserIdFromToken();
            return ResponseEntity.ok(ReviewResponse.getReviewResponses(repo.findByUserId(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Collections.singleton(new ReviewResponse("error", e.getMessage()))
            );
        }
    }
}