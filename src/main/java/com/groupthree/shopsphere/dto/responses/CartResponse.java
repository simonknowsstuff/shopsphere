package com.groupthree.shopsphere.dto.responses;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CartResponse {
    @NotNull
    private String status;
    @NotEmpty
    private String message;

    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;

    public CartResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public CartResponse(String status, String message, Long id, Long userId, Long productId, Integer quantity) {
        this.status = status;
        this.message = message;
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
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

    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
