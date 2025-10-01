package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Product;
import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.models.Order;
import com.groupthree.shopsphere.models.OrderItem;
import com.groupthree.shopsphere.repository.OrderItemRepository;
import com.groupthree.shopsphere.repository.OrderRepository;
import com.groupthree.shopsphere.repository.ProductRepository;
import com.groupthree.shopsphere.repository.UserRepository;

import org.springframework.data.relational.core.conversion.DbActionExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = {"/orders/", "/orders"})
public class OrderController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductRepository productRepo;

    public OrderController(OrderRepository orderRepo, 
                           OrderItemRepository orderItemRepo, 
                           UserRepository userRepository,
                           ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.userRepository = userRepository;
        this.productRepo = productRepo;
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @PostMapping(value = {"/", ""})
    public Order createOrder(@RequestBody List<OrderItem> items) {
        Long userId = getUserIdFromToken();

        Order order = new Order(userId, BigDecimal.ZERO, "PENDING");
        order = orderRepo.save(order);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            Product product = productRepo.findById(item.getProductId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            item.setPrice(BigDecimal.valueOf(product.getPrice()));

            BigDecimal itemTotal = BigDecimal.valueOf(product.getPrice())
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);

            item.setOrderId(order.getId());
            orderItemRepo.save(item);
        }
        order.setTotalAmount(total);
        return orderRepo.save(order);
    }

    @GetMapping(value = {"/user/", "/user"})
    public List<Order> getOrdersByUser() {
        Long userId = getUserIdFromToken();
        return orderRepo.findByUserId(userId);
    }

    @GetMapping(value = {"/{orderId}/items/", "/{orderId}/items"})
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        return orderItemRepo.findByOrderId(orderId);
    }

    @ExceptionHandler(DbActionExecutionException.class)
    public ResponseEntity<String> handleDbActionExecutionException(DbActionExecutionException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }
}
