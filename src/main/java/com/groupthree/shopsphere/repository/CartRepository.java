package com.groupthree.shopsphere.repository;

import com.groupthree.shopsphere.models.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CartRepository extends CrudRepository<Cart, Long> {
    List<Cart> findByUserId(Long id);
}
