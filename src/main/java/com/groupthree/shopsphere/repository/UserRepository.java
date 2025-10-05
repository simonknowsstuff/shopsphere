package com.groupthree.shopsphere.repository;

import com.groupthree.shopsphere.models.User;

import org.springframework.data.repository.CrudRepository;

// Interface to interact with user data
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
