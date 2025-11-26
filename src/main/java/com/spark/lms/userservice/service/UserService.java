package com.spark.lms.userservice.service;

import java.util.List;

import com.spark.lms.userservice.dto.AuthResponse;
import com.spark.lms.userservice.dto.LoginRequest;
import com.spark.lms.userservice.dto.RegistrationRequest;
import com.spark.lms.userservice.entity.User;

public interface UserService {
    User register(RegistrationRequest req);
    AuthResponse login(LoginRequest req);
    User getUser(Long id);
    List<User> listUsers();
    User updateUser(Long id, User updated);
    void deleteUser(Long id);
}
