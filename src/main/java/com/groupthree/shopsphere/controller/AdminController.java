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
import java.util.ArrayList;
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

    private List<ProductResponse> productToResponse(Iterable<Product> products) {
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

    private List<OrderResponse> getOrderResponses(Iterable<Order> orders) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order : orders) {
            OrderResponse orderResponse = new OrderResponse(
                    "success",
                    "Order obtained successfully",
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus()
            );
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }

    private List<ReviewResponse> getReviewResponses(Iterable<Review> reviews) {
        List<ReviewResponse> reviewResponses = new ArrayList<>();
        for (Review review : reviews) {
            ReviewResponse reviewResponse = new ReviewResponse(
                    "success",
                    "Review obtained successfully",
                    review.getId(),
                    review.getUserId(),
                    review.getProductId(),
                    review.getReview()
            );
            reviewResponses.add(reviewResponse);
        }
        return reviewResponses;
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
    public List<UserResponse> getUsers() {
        try {
            Iterable<User> users = userRepo.findAll();
            List<UserResponse> userResponses = new ArrayList<>();
            for (User user : users) {
                UserResponse userResponse = new UserResponse(
                        "success",
                        "User obtained successfully",
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRole()
                );
                userResponses.add(userResponse);
            }
            return userResponses;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return Collections.singletonList(new UserResponse("error", e.getMessage()));
        }
    }

    @GetMapping(value = {"/users/{userId}/", "/users/{userId}"})
    public UserResponse getUser(@PathVariable Long userId) {
        try {
            User user = userRepo.findById(userId).orElseThrow();
            return new UserResponse(
                    "success",
                    "User obtained successfully",
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole()
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new UserResponse("error", e.getMessage());
        }
    }

    @PostMapping(value = {"/users/", "/users"})
    public UserResponse addUser(@Valid @RequestBody User user) {
        try {
            userRepo.save(user);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new UserResponse("error", e.getMessage());
        }
        return new UserResponse("success", "User added successfully");
    }

    @PutMapping(value = {"/users/{userId}/", "/users/{userId}"})
    public UserResponse updateUser(@Valid @RequestBody User user, @PathVariable Long userId) {
        try {
            userRepo.findById(userId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
            );
            user.setId(userId);
            userRepo.save(user);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new UserResponse("error", e.getMessage());
        }
        return new UserResponse("success", "User updated successfully");
    }

    @DeleteMapping(value = {"/users/{userId}/", "/users/{userId}"})
    public UserResponse deleteUser(@PathVariable Long userId) {
        try {
            userRepo.deleteById(userId);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new UserResponse("error", e.getMessage());
        }
        return new UserResponse("success", "User deleted successfully");
    }

    // Products
    @GetMapping(value = {"/products/", "/products"})
    public Iterable<ProductResponse> getProducts() {
        try {
            return productToResponse(productRepo.findAll());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return Collections.singletonList(new ProductResponse("error", e.getMessage()));
        }
    }

    @PostMapping(value = {"/products/", "/products"})
    public ProductResponse addProduct(@Valid @RequestBody List<Product> products) {
        try {
            productRepo.saveAll(products);
        } catch (Exception e) {
            return new ProductResponse("error", e.getMessage());
        }
        return new ProductResponse("success", "Products added successfully");
    }

    @DeleteMapping(value = {"/products/{productId}/", "/products/{productId}"})
    public ProductResponse deleteProduct(@PathVariable Long productId) {
        try {
            productRepo.deleteById(productId);
        } catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            return new ProductResponse("error", e.getMessage());
        }
        return new ProductResponse("success", "Product deleted successfully");
    }

    // Orders
    @GetMapping(value = {"/orders/", "/orders"})
    public List<OrderResponse> getOrders() {
        try {
            Iterable<Order> orders = orderRepo.findAll();
            return getOrderResponses(orders);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return Collections.singletonList(new OrderResponse("error", e.getMessage()));
        }
    }

    @GetMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public OrderResponse getOrder(@PathVariable Long orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
            );
            return new OrderResponse(
                    "success",
                    "Order obtained successfully",
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus()
            );
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return new OrderResponse("error", e.getMessage());
        }
    }

    @GetMapping(value = {"/orders/users/{userId}/", "/orders/users/{userId}"})
    public List<OrderResponse> getOrdersByUser(@PathVariable Long userId) {
        try {
            List<Order> orders = orderRepo.findByUserId(userId);
            return getOrderResponses(orders);
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return Collections.singletonList(new OrderResponse("error", e.getMessage()));
        }
    }

    @PostMapping(value = {"/orders/users/{userId}/", "/orders/users/{userId}"})
    public OrderResponse addOrderForUser(@Valid @RequestBody List<OrderItem> orders, @PathVariable Long userId) {
        try {
            Order order = new Order(userId, BigDecimal.ZERO, "PENDING");
            order = orderRepo.save(order);

            BigDecimal total = BigDecimal.ZERO;
            return getOrdersSum(orders, order, total);
        } catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            return new OrderResponse("error", e.getMessage());
        }
    }

    @PutMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public OrderResponse updateOrderItems(@Valid @RequestBody List<OrderItem> orders, @PathVariable Long orderId) {
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
            return new OrderResponse("error", e.getMessage());
        }
    }

    @PutMapping(value = {"/orders/update/{orderId}/", "/orders/update/{orderId}"})
    public OrderResponse updateOrder(@Valid @RequestBody Order order, @PathVariable Long orderId) {
        try {
            orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
            );
            order.setId(orderId);
            orderRepo.save(order);
            return new OrderResponse(
                    "success",
                    "Order updated successfully",
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus()
            );
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return new OrderResponse("error", e.getMessage());
        }
    }

    @DeleteMapping(value = {"/orders/{orderId}/", "/orders/{orderId}"})
    public OrderResponse deleteOrder(@PathVariable Long orderId) {
        try {
            Order order = orderRepo.findById(orderId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
            orderRepo.delete(order);
            return new OrderResponse(
                    "success",
                    "Order deleted successfully"
            );
        }
        catch (ResponseStatusException e) {
            throw e;
        }
        catch (Exception e) {
            return new OrderResponse("error", e.getMessage());
        }
    }

    // Reviews
    @GetMapping(value = {"/reviews/", "/reviews"})
    public List<ReviewResponse> getReviews() {
        try {
            Iterable<Review> reviews = reviewRepo.findAll();
            return getReviewResponses(reviews);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return Collections.singletonList(new ReviewResponse("error", e.getMessage()));
        }
    }

    @GetMapping(value = {"/reviews/users/{userId}/", "/reviews/users/{userId}"})
    public List<ReviewResponse> getReviewsByUser(@PathVariable Long userId) {
        try {
            List<Review> reviews = reviewRepo.findByUserId(userId);
            return getReviewResponses(reviews);
        }
        catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return Collections.singletonList(new ReviewResponse("error", e.getMessage()));
        }
    }

    @PostMapping(value = {"/reviews/users/{userId}/", "/reviews/users/{userId}"})
    public ReviewResponse addReviewForUser(@Valid @RequestBody Review review, @PathVariable Long userId) {
        try {
            review.setUserId(userId);
            reviewRepo.save(review);
            return new ReviewResponse(
                    "success",
                    "Review added successfully",
                    review.getId(),
                    review.getUserId(),
                    review.getProductId(),
                    review.getReview()
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return new ReviewResponse("error", e.getMessage());
        }
    }

    @PutMapping(value = {"/reviews/{reviewId}/", "/reviews/{reviewId}"})
    public ReviewResponse updateReview(@Valid @RequestBody Review review, @PathVariable Long reviewId) {
        try {
            reviewRepo.findById(reviewId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found")
            );
            review.setId(reviewId);
            reviewRepo.save(review);
            return new ReviewResponse(
                    "success",
                    "Review added successfully",
                    review.getId(),
                    review.getUserId(),
                    review.getProductId(),
                    review.getReview()
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return new ReviewResponse("error", e.getMessage());
        }
    }

    @DeleteMapping(value = {"/reviews/{reviewId}/", "/reviews/{reviewId}"})
    public ReviewResponse deleteReview(@PathVariable Long reviewId) {
        try {
            Review review = reviewRepo.findById(reviewId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
            reviewRepo.delete(review);
            return new ReviewResponse("success", "Review deleted successfully");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            return new ReviewResponse("error", e.getMessage());
        }
    }
}
