package com.groupthree.shopsphere.controller;
import com.groupthree.shopsphere.dto.requests.LoginRequest;
import com.groupthree.shopsphere.dto.requests.RegisterRequest;
import com.groupthree.shopsphere.dto.responses.AuthResponse;

import com.groupthree.shopsphere.models.User;
import com.groupthree.shopsphere.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserRepository repo;
    BCryptPasswordEncoder passwordEncoder;

    public AuthController(UserRepository repo) {
        this.repo=repo;
        this.passwordEncoder = new BCryptPasswordEncoder();}

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request){
        if (repo.findByEmail(request.getEmail())!=null){
            return new AuthResponse("error","email already exists",null);
        }
        User user=new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        repo.save(user);
        Set<String> roles=new HashSet<>();
        roles.add("Customer");
        return new AuthResponse("success","Registered",roles);
    }

    @PostMapping("/register/vendor")
    public AuthResponse vendorRegister(@RequestBody RegisterRequest request){
        if (repo.findByEmail(request.getEmail())!=null){
            return new AuthResponse("error","email already exists",null);
        }

        User user=new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<String> roles=new HashSet<>();
        roles.add("customer");
        roles.add("Vendor");
        user.setRole(roles);
        repo.save(user);
        return new AuthResponse("success","Registered",roles);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request){
        User user=repo.findByEmail(request.getEmail());
        if (user==null||!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            return new AuthResponse("error","Invalid email or password",null);
        }
        return new AuthResponse("success","Logged in",user.getRole());
    }

}
