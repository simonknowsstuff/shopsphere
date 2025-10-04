package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Review;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.ReviewRepository;

import com.groupthree.shopsphere.repository.UserRepository;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public Iterable<Review> getReviews(@PathVariable Long productId) {
        return repo.findByProductId(productId);
    }

    @PostMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public Review addReview(@RequestBody Review review, @PathVariable Long productId) {
        Long userId = getUserIdFromToken();
        review.setUserId(userId);
        review.setProductId(productId);
        validateReview(review);
        return repo.save(review);
    }

    @PutMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public Review updateReview(@RequestBody Review review, @PathVariable Long productId) {
        Long userId = getUserIdFromToken();
        Review existing = repo.findByUserIdAndProductId(userId, productId).orElseThrow();
        existing.setReview(review.getReview());
        existing.setRating(review.getRating());
        validateReview(existing);
        return repo.save(existing);
    }

    @DeleteMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ResponseEntity<String> deleteReview(@PathVariable Long productId) {
        Long userId = getUserIdFromToken();
        Review review = repo.findByUserIdAndProductId(userId, productId).orElseThrow();
        repo.delete(review);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Review deleted");
    }

    @GetMapping(value = {"/user/{userId}/", "/user/{userId}"})
    public Iterable<Review> getReviewsByUser(@PathVariable Long userId) {
        return repo.findByUserId(userId);
    }

    @GetMapping(value={"/user","/user/"})
    public Iterable<Review> getOurReviews() {
        Long userId = getUserIdFromToken();
        return repo.findByUserId(userId);
    }

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<String> handleDbActionExecutionException(DbActionExecutionException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
