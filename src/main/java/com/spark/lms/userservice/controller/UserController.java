package com.spark.lms.userservice.controller;

import com.spark.lms.userservice.dto.*;
import com.spark.lms.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest req) {
        return userService.register(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return userService.login(req);
    }

    @GetMapping("/{id}")
    public UserResponse getUser(
            @PathVariable String id,
            @RequestHeader("Authorization") String authHeader
    ) {
        // Remove "Bearer "
        String token = authHeader.substring(7);

        return userService.getUserById(id);
    }

}
