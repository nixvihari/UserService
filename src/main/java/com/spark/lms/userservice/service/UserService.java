package com.spark.lms.userservice.service;

import com.spark.lms.userservice.dto.RegisterRequest;
import com.spark.lms.userservice.dto.UpdateUserRequest;

import java.util.List;

import com.spark.lms.userservice.dto.LoginRequest;
import com.spark.lms.userservice.dto.LoginResponse;
import com.spark.lms.userservice.dto.UserResponse;

public interface UserService {
    UserResponse register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    UserResponse getUserById(String id);
    
    //CRUD OF USER SERVICE
    List<UserResponse> getAllUsers();
    UserResponse updateUser(String id, UpdateUserRequest req);
    void deleteUser(String id);
    UserResponse getUserByEmail(String email);

}
