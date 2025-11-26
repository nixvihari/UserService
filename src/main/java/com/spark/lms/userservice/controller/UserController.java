package com.spark.lms.userservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spark.lms.userservice.dto.UserResponse;
import com.spark.lms.userservice.entity.User;
import com.spark.lms.userservice.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService svc;
    @Autowired
    public UserController(UserService svc) { this.svc = svc; }

    @GetMapping
    public ResponseEntity<List<UserResponse>> list() {
        List<User> list = svc.listUsers();
        List<UserResponse> res = list.stream().map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole())).collect(Collectors.toList());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable Long id) {
        User u = svc.getUser(id);
        return ResponseEntity.ok(new UserResponse(u.getId(), u.getUsername(), u.getEmail(), u.getRole()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody User update) {
        User updated = svc.updateUser(id, update);
        return ResponseEntity.ok(new UserResponse(updated.getId(), updated.getUsername(), updated.getEmail(), updated.getRole()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        svc.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

