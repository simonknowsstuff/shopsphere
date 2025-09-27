package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.models.Order;
import com.groupthree.shopsphere.models.OrderItem;
import com.groupthree.shopsphere.repository.OrderItemRepository;
import com.groupthree.shopsphere.repository.OrderRepository;
import com.groupthree.shopsphere.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    public OrderController(OrderRepository orderRepo, 
                           OrderItemRepository orderItemRepo, 
                           UserRepository userRepository) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.userRepository = userRepository;
    }

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @PostMapping
    public Order createOrder(@Valid @RequestBody List<OrderItem> items) {
        Long userId = getUserIdFromToken();
        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Calculate total amount for order.

        Order order = new Order(userId, total, "PENDING");
        order = orderRepo.save(order);

        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemRepo.save(item);
        }

        return order;
    }

    @GetMapping("/user")
    public List<Order> getOrdersByUser() {
        Long userId = getUserIdFromToken();
        return orderRepo.findByUserId(userId);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        return orderItemRepo.findByOrderId(orderId);
    }
}
