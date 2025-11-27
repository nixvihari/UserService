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

	public UserServiceImpl(UserRepository userRepo, JwtUtil jwtUtil) {
		super();
		this.userRepo = userRepo;
		this.jwtUtil = jwtUtil;
	}

	// -----------------------------
	// Register User
	// -----------------------------
	@Override
	public UserResponse register(RegisterRequest req) {
		if (userRepo.findByEmail(req.getEmail()).isPresent()) {
			throw new RuntimeException("Email already exists!");
		}

		User user = new User();
		user.setEmail(req.getEmail());
		user.setName(req.getName());
		user.setHashedPassword(encoder.encode(req.getPassword()));
		user.setRole(req.getRole());

		userRepo.save(user);

		UserResponse response = new UserResponse();
		response.setId(user.getId().toString());
		response.setEmail(user.getEmail());
		response.setName(user.getName());
		response.setRole(user.getRole());

		return response;

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

		UserResponse response = new UserResponse();
		response.setId(user.getId().toString());
		response.setEmail(user.getEmail());
		response.setName(user.getName());
		response.setRole(user.getRole());
		
		return response;

	}
}
