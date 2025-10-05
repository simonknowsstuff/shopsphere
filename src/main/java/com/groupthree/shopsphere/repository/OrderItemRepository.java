package com.groupthree.shopsphere.repository;

import com.groupthree.shopsphere.models.OrderItem;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderItemRepository extends CrudRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}
