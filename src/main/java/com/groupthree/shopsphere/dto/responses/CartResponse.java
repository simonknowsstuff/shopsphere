package com.groupthree.shopsphere.dto.responses;

import com.groupthree.shopsphere.models.Cart;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    public static CartResponse fromCart(String message, Cart cart) {
        return new CartResponse(
                "success",
                message,
                cart.getId(),
                cart.getUserId(),
                cart.getProductId(),
                cart.getQuantity()
        );
    }

    public static List<CartResponse> getCartResponses(Iterable<Cart> carts) {
        List<CartResponse> cartResponses = new ArrayList<>();
        for (Cart cart : carts) {
            CartResponse cartResponse = new CartResponse(
                    "success",
                    "Cart obtained successfully",
                    cart.getId(),
                    cart.getUserId(),
                    cart.getProductId(),
                    cart.getQuantity()
            );
            cartResponses.add(cartResponse);
        }
        return cartResponses;
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
