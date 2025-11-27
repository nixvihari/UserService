package com.spark.lms.userservice.service;

import com.spark.lms.userservice.dto.RegisterRequest;
import com.spark.lms.userservice.dto.LoginRequest;
import com.spark.lms.userservice.dto.LoginResponse;
import com.spark.lms.userservice.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    UserResponse getUserById(String id);
}
