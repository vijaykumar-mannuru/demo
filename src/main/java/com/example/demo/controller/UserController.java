package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.dtos.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // ================= CREATE USER =================
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {

        User saved = userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("User created successfully", saved));
    }

    // ================= GET ALL USERS =================
    @GetMapping
    public ResponseEntity<ApiResponse> getAllUsers() {

        List<User> users = userRepository.findAll();

        return ResponseEntity.ok(
                ApiResponse.ok("All users fetched", users)
        );
    }

    // ================= GET USER BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return ResponseEntity.ok(
                ApiResponse.ok("User fetched", user)
        );
    }
}