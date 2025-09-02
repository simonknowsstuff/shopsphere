package com.groupthree.shopsphere.repository;

import com.groupthree.shopsphere.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}