package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.*;
import com.groupthree.shopsphere.repository.*;

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

    public Long getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        return user.getId();
    }

    @PostMapping(value = {"/", ""})
    public Order createOrder() {
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
        return orderRepo.save(order);
    }

    @GetMapping(value = {"/", ""})
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