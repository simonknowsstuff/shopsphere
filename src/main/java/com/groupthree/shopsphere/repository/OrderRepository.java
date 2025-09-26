package com.groupthree.shopsphere.repository;

import com.groupthree.shopsphere.models.Order;
import com.groupthree.shopsphere.models.User;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
