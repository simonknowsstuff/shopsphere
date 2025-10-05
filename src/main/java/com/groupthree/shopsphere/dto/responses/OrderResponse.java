package com.groupthree.shopsphere.dto.responses;

import com.groupthree.shopsphere.models.Order;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderResponse {
    @NotNull
    private String status;
    @NotEmpty
    private String message;

    private Long id;
    private Long userId;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;

    public OrderResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public OrderResponse(String status, String message, Long id, Long userId, LocalDateTime orderDate, BigDecimal totalAmount, String orderStatus) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
    }

    public static List<OrderResponse> getOrderResponses(Iterable<Order> orders) {
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
