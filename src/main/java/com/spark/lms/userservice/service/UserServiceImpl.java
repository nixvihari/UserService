package com.spark.lms.userservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spark.lms.userservice.dto.AuthResponse;
import com.spark.lms.userservice.dto.LoginRequest;
import com.spark.lms.userservice.dto.RegistrationRequest;
import com.spark.lms.userservice.entity.User;
import com.spark.lms.userservice.entity.Role;
import com.spark.lms.userservice.repository.UserRepository;
import com.spark.lms.userservice.util.JwtUtil;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserServiceImpl(UserRepository repo,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User register(RegistrationRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            throw new IllegalArgumentException("Email and password required");
        }
        if (repo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = new User();
        u.setUsername(req.getName());
        u.setEmail(req.getEmail());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        // Use Role from entity package, default to STUDENT when null
        u.setRole(req.getRole() == null ? Role.STUDENT : req.getRole());
        return repo.save(u);
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        if (req.getEmail() == null || req.getPassword() == null) {
            throw new IllegalArgumentException("Email and password required");
        }
        User user = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Build token using username and role-name (handle null role)
        String roleString = (user.getRole() == null) ? Role.STUDENT.name() : user.getRole().name();
        String token = jwtUtil.generateToken(user.getUsername(), roleString);

        return new AuthResponse(token, user.getId(), user.getUsername(), user.getRole());
    }

    @Override
    public User getUser(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Override
    public List<User> listUsers() {
        return repo.findAll();
    }

    @Override
    public User updateUser(Long id, User updated) {
        User existing = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (updated.getUsername() != null) existing.setUsername(updated.getUsername());
        if (updated.getEmail() != null) existing.setEmail(updated.getEmail());
        if (updated.getPassword() != null) existing.setPassword(passwordEncoder.encode(updated.getPassword()));
        if (updated.getRole() != null) existing.setRole(updated.getRole());
        return repo.save(existing);
    }

    @Override
    public void deleteUser(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("User not found");
        repo.deleteById(id);
    }
}
