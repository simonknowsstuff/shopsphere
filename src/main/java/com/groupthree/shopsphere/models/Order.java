package com.groupthree.shopsphere.models;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PastOrPresent;

@Table("ORDERS")
public class Order {
    @Id
    private Long id;

    @NotNull
    private Long userId;
    
    @NotNull
    @PastOrPresent
    private LocalDateTime orderDate;
    
    @NotNull
    @Min(0)
    private BigDecimal totalAmount;

    @NotNull
    private String status;

    public Order(){}

    public Order(Long userId, BigDecimal total, String pending) {
        this.userId = userId;
        this.totalAmount = total;
        this.status = pending;
        this.orderDate = LocalDateTime.now();
    }

    // Getters and Setters
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

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
