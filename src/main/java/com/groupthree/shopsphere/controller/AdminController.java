package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.responses.OrderResponse;
import com.groupthree.shopsphere.dto.responses.ProductResponse;
import com.groupthree.shopsphere.dto.responses.ReviewResponse;
import com.groupthree.shopsphere.dto.responses.UserResponse;
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
import java.util.Collections;
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

    private OrderResponse getOrdersSum(@RequestBody @Valid List<OrderItem> orders, Order order, BigDecimal total) {
        try {
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
            return new OrderResponse(
                    "success",
                    "Order saved successfully",
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus()
            );
        } catch (Exception e) {
            return new OrderResponse("error", e.getMessage());
        }
    }

    // Users
    @GetMapping(value = {"/users/", "/users"})
    public ResponseEntity<List<UserResponse>> getUsers() {
        try {
            return ResponseEntity.ok(UserResponse.getUserResponses(userRepo.findAll()));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(new UserResponse("error", e.getMessage())));
        }
    }

    @GetMapping(value = {"/users/{userId}/", "/users/{userId}"})
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        try {
            User user = userRepo.findById(userId).orElseThrow();
            return ResponseEntity.ok(UserResponse.fromUser("User obtained successfully", user));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new UserResponse("error", e.getMessage()));
        }
    }

    @PostMapping(value = {"/users/", "/users"})
    public ResponseEntity<UserResponse> addUser(@Valid @RequestBody User user) {
        try {
            userRepo.save(user);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserResponse("success", "User added successfully"));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new UserResponse("error", e.getMessage()));
        }
    }

    @PutMapping(value = {"/users/{userId}/", "/users/{userId}"})
    public ResponseEntity<UserResponse> updateUser(@Valid @RequestBody User user, @PathVariable Long userId) {
        try {
            userRepo.findById(userId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
            );
            user.setId(userId);
            userRepo.save(user);
            return ResponseEntity.ok(new UserResponse("success", "User updated successfully"));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new UserResponse("error", e.getMessage()));
        }
    }

    @DeleteMapping(value = {"/users/{userId}/", "/users/{userId}"})
    public ResponseEntity<UserResponse> deleteUser(@PathVariable Long userId) {
        try {
            userRepo.deleteById(userId);
            return ResponseEntity.ok(new UserResponse("success", "User deleted successfully"));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new UserResponse("error", e.getMessage()));
        }
    }

    // Products
    @GetMapping(value = {"/products/", "/products"})
    public ResponseEntity<Iterable<ProductResponse>> getProducts() {
        try {
            return ResponseEntity.ok(ProductResponse.getProductResponses(productRepo.findAll()));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(new ProductResponse("error", e.getMessage())));
        }
    }

    @PostMapping(value = {"/products/", "/products"})
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody List<Product> products) {
        try {
            productRepo.saveAll(products);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ProductResponse("success", "Products added successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ProductResponse("error", e.getMessage()));
        }
    }

    @DeleteMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ResponseEntity<ProductResponse> deleteProduct(@PathVariable Long productId) {
        try {
            Product product = productRepo.findById(productId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            // Delete all reviews associated with the product first:
            List<Review> productReviews = reviewRepo.findByProductId(productId);
            reviewRepo.deleteAll(productReviews);
            // Then delete product:
            productRepo.delete(product);
        return ResponseEntity.ok(new ProductResponse("success", "Product and associated reviews deleted successfully"));
    } catch (RuntimeException e) {
        throw e;
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ProductResponse("error", e.getMessage()));
    }
}

    // Orders
    @GetMapping(value = {"/orders/", "/orders"})
    public ResponseEntity<List<OrderResponse>> getOrders() {
        try {
            Iterable<Order> orders = orderRepo.findAll();
            return ResponseEntity.ok(OrderResponse.getOrderResponses(orders));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(new OrderResponse("error", e.getMessage())));
        }
    }

    @GetMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
            );
            return ResponseEntity.ok(new OrderResponse(
                    "success",
                    "Order obtained successfully",
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus()
            ));
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OrderResponse("error", e.getMessage()));
        }
    }

    @GetMapping(value = {"/orders/users/{userId}/", "/orders/users/{userId}"})
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Long userId) {
        try {
            List<Order> orders = orderRepo.findByUserId(userId);
            return ResponseEntity.ok(OrderResponse.getOrderResponses(orders));
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(new OrderResponse("error", e.getMessage())));
        }
    }

    @PostMapping(value = {"/orders/users/{userId}/", "/orders/users/{userId}"})
    public ResponseEntity<OrderResponse> addOrderForUser(@Valid @RequestBody List<OrderItem> orders, @PathVariable Long userId) {
        try {
            Order order = new Order(userId, BigDecimal.ZERO, "PENDING");
            order = orderRepo.save(order);

            BigDecimal total = BigDecimal.ZERO;
            OrderResponse response = getOrdersSum(orders, order, total);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OrderResponse("error", e.getMessage()));
        }
    }

    @PutMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public ResponseEntity<OrderResponse> updateOrderItems(@Valid @RequestBody List<OrderItem> orders, @PathVariable Long orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            order.setTotalAmount(BigDecimal.ZERO);
            orderRepo.save(order);
            BigDecimal total = BigDecimal.ZERO;
            OrderResponse response = getOrdersSum(orders, order, total);
            return ResponseEntity.ok(response);
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OrderResponse("error", e.getMessage()));
        }
    }

    @PutMapping(value = {"/orders/update/{orderId}/", "/orders/update/{orderId}"})
    public ResponseEntity<OrderResponse> updateOrder(@Valid @RequestBody Order order, @PathVariable Long orderId) {
        try {
            orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
            );
            order.setId(orderId);
            orderRepo.save(order);
            return ResponseEntity.ok(new OrderResponse(
                    "success",
                    "Order updated successfully",
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus()
            ));
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OrderResponse("error", e.getMessage()));
        }
    }

    @DeleteMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public ResponseEntity<OrderResponse> deleteOrder(@PathVariable Long orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            orderRepo.delete(order);
            return ResponseEntity.ok(new OrderResponse(
                    "success",
                    "Order deleted successfully"
            ));
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OrderResponse("error", e.getMessage()));
        }
    }

    // Reviews
    @GetMapping(value = {"/reviews/", "/reviews"})
    public ResponseEntity<List<ReviewResponse>> getReviews() {
        try {
            Iterable<Review> reviews = reviewRepo.findAll();
            return ResponseEntity.ok(ReviewResponse.getReviewResponses(reviews));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(new ReviewResponse("error", e.getMessage())));
        }
    }

    @GetMapping(value = {"/reviews/users/{userId}/", "/reviews/users/{userId}"})
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable Long userId) {
        try {
            List<Review> reviews = reviewRepo.findByUserId(userId);
            return ResponseEntity.ok(ReviewResponse.getReviewResponses(reviews));
        }
        catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonList(new ReviewResponse("error", e.getMessage())));
        }
    }

    @PostMapping(value = {"/reviews/users/{userId}/", "/reviews/users/{userId}"})
    public ResponseEntity<ReviewResponse> addReviewForUser(@Valid @RequestBody Review review, @PathVariable Long userId) {
        try {
            review.setUserId(userId);
            reviewRepo.save(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(ReviewResponse.fromReview(review, "Review added successfully"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReviewResponse("error", e.getMessage()));
        }
    }

    @PutMapping(value = {"/reviews/{reviewId}/", "/reviews/{reviewId}"})
    public ResponseEntity<ReviewResponse> updateReview(@Valid @RequestBody Review review, @PathVariable Long reviewId) {
        try {
            reviewRepo.findById(reviewId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found")
            );
            review.setId(reviewId);
            reviewRepo.save(review);
            return ResponseEntity.ok(ReviewResponse.fromReview(review, "Review updated successfully"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReviewResponse("error", e.getMessage()));
        }
    }

    @DeleteMapping(value = {"/reviews/{reviewId}/", "/reviews/{reviewId}"})
    public ResponseEntity<ReviewResponse> deleteReview(@PathVariable Long reviewId) {
        try {
            Review review = reviewRepo.findById(reviewId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
            reviewRepo.delete(review);
            return ResponseEntity.ok(new ReviewResponse("success", "Review deleted successfully"));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReviewResponse("error", e.getMessage()));
        }
    }
}