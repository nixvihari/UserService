package com.spark.lms.userservice.dto;

public class UpdateUserRequest {

    private final String name;
    private final String email;
    private final String role; // optional, only admin can change

    // Constructor injection
    public UpdateUserRequest(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters (no setters, immutable DTO)
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
