package com.groupthree.shopsphere.repository;
// Interface for interacting with users data.
import com.groupthree.shopsphere.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User,Integer> {
    User findByEmail(String email);
}
