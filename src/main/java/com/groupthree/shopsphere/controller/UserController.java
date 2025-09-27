package com.groupthree.shopsphere.controller;

import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return repo.save(user);
    }

    @GetMapping
    public Iterable<User> findAll() {
        return repo.findAll();
    }
}
