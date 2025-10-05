package com.groupthree.shopsphere.repository;

import com.groupthree.shopsphere.models.Order;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);

}
