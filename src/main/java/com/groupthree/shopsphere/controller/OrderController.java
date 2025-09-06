package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.Order;
import com.groupthree.shopsphere.models.OrderItem;
import com.groupthree.shopsphere.repository.OrderItemRepository;
import com.groupthree.shopsphere.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    public OrderController(OrderRepository orderRepo, OrderItemRepository orderItemRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @PostMapping
    public Order createOrder(@RequestBody List<OrderItem> items, @RequestParam Long userId) {
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

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable Long userId) {
        return orderRepo.findByUserId(userId);
    }

    @GetMapping("/{orderId}/items")
    public List<OrderItem> getOrderItems(@PathVariable Long orderId) {
        return orderItemRepo.findByOrderId(orderId);
    }
}
