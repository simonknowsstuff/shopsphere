package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.*;
import com.groupthree.shopsphere.repository.UserRepository;
import com.groupthree.shopsphere.repository.ProductRepository;
import com.groupthree.shopsphere.repository.OrderRepository;
import com.groupthree.shopsphere.repository.ReviewRepository;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = {"/admin/", "/admin"})
public class AdminController {
    private final UserRepository userRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final ReviewRepository reviewRepo;

    public AdminController(UserRepository userRepo, ProductRepository productRepo, OrderRepository orderRepo, ReviewRepository reviewRepo) {
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.reviewRepo = reviewRepo;
    }

    private ResponseEntity<Order> getOrdersSum(@RequestBody @Valid List<OrderItem> orders, Order order, BigDecimal total) {
        for (OrderItem item : orders) {
            Product product = productRepo.findById(item.getProductId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            item.setPrice(BigDecimal.valueOf(product.getPrice()));

            BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);

            item.setOrderId(order.getId());
        }
        order.setTotalAmount(total);
        orderRepo.save(order);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(order);
    }

    // Users
    @GetMapping(value = {"/users/", "/users"})
    public Iterable<User> getUsers() {
        return userRepo.findAll();
    }

    @GetMapping(value = {"/users/{userId}/", "/users/{userId}"})
    public User getUser(@PathVariable Long userId) {
        return userRepo.findById(userId).orElseThrow();
    }

    @PostMapping(value = {"/users/", "/users"})
    public ResponseEntity<String> addUser(@Valid @RequestBody User user) {
        try {
            userRepo.save(user);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error adding user: " + e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User added");
    }

    @DeleteMapping(value = {"/users/{userId}/", "/users/{userId}"})
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userRepo.deleteById(userId);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting user: " + e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User deleted");
    }

    // Products
    @GetMapping(value = {"/products/", "/products"})
    public Iterable<Product> getProducts() {
        return productRepo.findAll();
    }

    @PostMapping(value = {"/products/", "/products"})
    public ResponseEntity<String> addProduct(@Valid @RequestBody List<Product> products) {
        try {
            productRepo.saveAll(products);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error adding products: " + e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Products added");
    }

    @DeleteMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        try {
            productRepo.deleteById(productId);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting product: " + e.getMessage());
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Product deleted");
    }

    // Orders
    @GetMapping(value = {"/orders/", "/orders"})
    public Iterable<Order> getOrders() {
        return orderRepo.findAll();
    }

    @GetMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
            );
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(order);
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error getting order: " + e.getMessage());
        }
    }

    @GetMapping(value = {"/orders/users/{userId}/", "/orders/users/{userId}"})
    public ResponseEntity<?> getOrdersByUser(@PathVariable Long userId) {
        try {
            List<Order> orders = orderRepo.findByUserId(userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(orders);
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error getting orders for user: " + e.getMessage());
        }
    }

    @PostMapping(value = {"/orders/users/{userId}/", "/orders/users/{userId}"})
    public ResponseEntity<?> addOrderForUser(@Valid @RequestBody List<OrderItem> orders, @PathVariable Long userId) {
        try {
            Order order = new Order(userId, BigDecimal.ZERO, "PENDING");
            order = orderRepo.save(order);

            BigDecimal total = BigDecimal.ZERO;
            return getOrdersSum(orders, order, total);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error adding order: " + e.getMessage());
        }
    }

    @PutMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public ResponseEntity<?> updateOrderItems(@Valid @RequestBody List<OrderItem> orders, @PathVariable Long orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            order.setTotalAmount(BigDecimal.ZERO);
            orderRepo.save(order);
            BigDecimal total = BigDecimal.ZERO;
            return getOrdersSum(orders, order, total);
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error adding order: " + e.getMessage());
        }
    }

    @PutMapping(value = {"/orders/update/{orderId}/", "/orders/update/{orderId}"})
    public ResponseEntity<String> updateOrder(@Valid @RequestBody Order order, @PathVariable Long orderId) {
        try {
            orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
            );
            order.setId(orderId);
            orderRepo.save(order);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Order updated");
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error updating order: " + e.getMessage());
        }
    }

    @DeleteMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            orderRepo.delete(order);
            return ResponseEntity.status(HttpStatus.OK).body("Order deleted");
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting order: " + e.getMessage());
        }
    }

    // Reviews
    @GetMapping(value = {"/reviews/", "/reviews"})
    public Iterable<Review> getReviews() {
        return reviewRepo.findAll();
    }

    @GetMapping(value = {"/reviews/users/{userId}/", "/reviews/users/{userId}"})
    public ResponseEntity<?> getReviewsByUser(@PathVariable Long userId) {
        try {
            List<Review> reviews = reviewRepo.findByUserId(userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reviews);
        }
        catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error getting reviews for user: " + e.getMessage());
        }
    }

    @PostMapping(value = {"/reviews/users/{userId}/", "/reviews/users/{userId}"})
    public ResponseEntity<?> addReviewForUser(@Valid @RequestBody Review review, @PathVariable Long userId) {
        try {
            review.setUserId(userId);
            reviewRepo.save(review);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Review added");
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error adding review: " + e.getMessage());
        }
    }

    @PutMapping(value = {"/reviews/{reviewId}/", "/reviews/{reviewId}"})
    public ResponseEntity<?> updateReview(@Valid @RequestBody Review review, @PathVariable Long reviewId) {
        try {
            reviewRepo.findById(reviewId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found")
            );
            review.setId(reviewId);
            reviewRepo.save(review);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("Review updated");
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error updating review: " + e.getMessage());
        }
    }

    @DeleteMapping(value = {"/reviews/{reviewId}/", "/reviews/{reviewId}"})
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        try {
            Review review = reviewRepo.findById(reviewId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
            reviewRepo.delete(review);
            return ResponseEntity.status(HttpStatus.OK).body("Review deleted");
        }
        catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error deleting review: " + e.getMessage());
        }
    }
}
