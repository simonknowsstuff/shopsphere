package com.groupthree.shopsphere.controller;
// Rest Controller for users.

import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return repo.save(user);
    }

    @GetMapping
    public Iterable<User> findAll() {
        return repo.findAll();
    }
}
