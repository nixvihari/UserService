package com.spark.lms.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.lms.userservice.dto.AuthResponse;
import com.spark.lms.userservice.dto.LoginRequest;
import com.spark.lms.userservice.dto.RegistrationRequest;
import com.spark.lms.userservice.dto.UserResponse;
import com.spark.lms.userservice.entity.User;
import com.spark.lms.userservice.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService svc;

    @Autowired
    public AuthController(UserService svc) {
        this.svc = svc;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegistrationRequest req) {
        User u = svc.register(req);
        UserResponse resp = new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole());
        return ResponseEntity.status(201).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse auth = svc.login(req);
        return ResponseEntity.ok(auth);
    }
}
