package com.spark.lms.userservice.serviceImpl;

import com.spark.lms.userservice.dto.*;
import com.spark.lms.userservice.entity.User;
import com.spark.lms.userservice.exception.InvalidPasswordException;
import com.spark.lms.userservice.exception.UserNotFoundException;
import com.spark.lms.userservice.repository.UserRepository;
import com.spark.lms.userservice.service.UserService;
import com.spark.lms.userservice.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // -----------------------------
    // Register User
    // -----------------------------
    @Override
    public UserResponse register(RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists!");
        }

        User user = User.builder()
                .email(req.getEmail())
                .name(req.getName())
                .hashedPassword(encoder.encode(req.getPassword()))
                .role(req.getRole())
                .build();

        userRepo.save(user);

        return UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }

    // -----------------------------
    // Login User (returns JWT)
    // -----------------------------
    @Override
    public LoginResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException("Email not registered!"));

        if (!encoder.matches(req.getPassword(), user.getHashedPassword())) {
            throw new InvalidPasswordException("Incorrect password!");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        return new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }

    // -----------------------------
    // Get User by ID
    // -----------------------------
    @Override
    public UserResponse getUserById(String id) {
        User user = userRepo.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return UserResponse.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .build();
    }
}
