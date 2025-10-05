package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.dto.responses.OrderResponse;
import com.groupthree.shopsphere.models.*;
import com.groupthree.shopsphere.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = {"/orders/", "/orders"})
public class OrderController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductRepository productRepo;
    private final CartRepository cartRepository;

    public OrderController(OrderRepository orderRepo,
                           OrderItemRepository orderItemRepo,
                           UserRepository userRepository,
                           ProductRepository productRepo, CartRepository cartRepository) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.userRepository = userRepository;
        this.productRepo = productRepo;
        this.cartRepository = cartRepository;
    }

    private Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @PostMapping(value = {"/", ""})
    public ResponseEntity<OrderResponse> createOrder() {
        try {
            Long userId = getUserIdFromToken();
            List<Cart> cartItems = cartRepository.findByUserId(userId);
            if (cartItems.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
            }
            Order order = new Order(userId, BigDecimal.ZERO, "PENDING");
            order = orderRepo.save(order);

            BigDecimal total = BigDecimal.ZERO;
            for (Cart cartItem : cartItems) {
                Product product = productRepo.findById(cartItem.getProductId()).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

                OrderItem item = new OrderItem();
                item.setProductId(product.getId());
                item.setQuantity(cartItem.getQuantity());
                item.setPrice(BigDecimal.valueOf(product.getPrice()));
                item.setOrderId(order.getId());

                BigDecimal itemTotal = item.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);

                orderItemRepo.save(item);
                cartRepository.delete(cartItem);
            }
            order.setTotalAmount(total);
            orderRepo.save(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(new OrderResponse(
                    "success",
                    "Order saved successfully",
                    order.getId(),
                    order.getUserId(),
                    order.getOrderDate(),
                    order.getTotalAmount(),
                    order.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OrderResponse("error", e.getMessage()));
        }
    }

    @GetMapping(value = {"/", ""})
    public ResponseEntity<List<OrderResponse>> getOrdersByUser() {
        try {
            Long userId = getUserIdFromToken();
            return ResponseEntity.ok(OrderResponse.getOrderResponses(orderRepo.findByUserId(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonList(new OrderResponse(
                    "error",
                    e.getMessage()
            )));
        }
    }

    @GetMapping(value = {"/{orderId}/items/", "/{orderId}/items"})
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        try {
            List<OrderItem> items = orderItemRepo.findByOrderId(orderId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}