package com.spark.lms.userservice.serviceImpl;

import com.spark.lms.userservice.dto.*;
import com.spark.lms.userservice.entity.User;
import com.spark.lms.userservice.exception.UserNotFoundException;
import com.spark.lms.userservice.repository.UserRepository;
import com.spark.lms.userservice.service.UserService;
import com.spark.lms.userservice.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepo;
	private final JwtUtil jwtUtil;
	private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public UserServiceImpl(UserRepository userRepo, JwtUtil jwtUtil) {
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

		return buildUserResponse(user);
	}

	// -----------------------------
	// Login User
	// -----------------------------
	@Override
	public LoginResponse login(LoginRequest req) {
		User user = userRepo.findByEmail(req.getEmail())
				.orElseThrow(() -> new UserNotFoundException("Email not registered!"));

		if (!encoder.matches(req.getPassword(), user.getHashedPassword())) {
			throw new RuntimeException("Incorrect password!");
		}

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
				.orElseThrow(() -> new UserNotFoundException("User not found!"));

		return buildUserResponse(user);
	}

	// -----------------------------
	// Add User (Admin only)
	// -----------------------------
//	@Override
//	public UserResponse addUser(AddUserRequest req) {
//		if (userRepo.findByEmail(req.getEmail()).isPresent()) {
//			throw new RuntimeException("Email already exists!");
//		}
//
//		User user = new User();
//		user.setEmail(req.getEmail());
//		user.setName(req.getName());
//		user.setHashedPassword(encoder.encode(req.getPassword()));
//		user.setRole(req.getRole());
//
//		userRepo.save(user);
//
//		return buildUserResponse(user);
//	}

	// -----------------------------
	// Update User
	// -----------------------------
	@Override
	public UserResponse updateUser(String id, UpdateUserRequest req) {
		UUID userId = UUID.fromString(id);
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found!"));

		if (req.getName() != null) user.setName(req.getName());
		if (req.getEmail() != null) user.setEmail(req.getEmail());
		if (req.getRole() != null) user.setRole(req.getRole());

		userRepo.save(user);
		return buildUserResponse(user);
	}

	// -----------------------------
	// Delete User
	// -----------------------------
	@Override
	public void deleteUser(String id) {
		UUID userId = UUID.fromString(id);
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found!"));
		userRepo.delete(user);
	}

	// -----------------------------
	// Search User by Email
	// -----------------------------
	@Override
	public UserResponse getUserByEmail(String email) {
		User user = userRepo.findByEmail(email)
				.orElseThrow(() -> new UserNotFoundException("User not found!"));
		return buildUserResponse(user);
	}

	// -----------------------------
	// Get All Users
	// -----------------------------
	@Override
	public List<UserResponse> getAllUsers() {
		return userRepo.findAll().stream()
				.map(this::buildUserResponse)
				.collect(Collectors.toList());
	}

	// -----------------------------
	// Helper to build UserResponse
	// -----------------------------
	private UserResponse buildUserResponse(User user) {
		UserResponse response = new UserResponse();
		response.setId(user.getId().toString());
		response.setEmail(user.getEmail());
		response.setName(user.getName());
		response.setRole(user.getRole());
		return response;
	}
}
