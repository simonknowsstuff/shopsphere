package com.groupthree.shopsphere.repository;

import com.groupthree.shopsphere.models.Cart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends CrudRepository<Cart, Long> {
    List<Cart> findByUserId(Long id);

    Optional<Cart> findByUserIdAndProductId(Long userId, Long productId);

    void deleteAllByUserId(Long userId);
}
