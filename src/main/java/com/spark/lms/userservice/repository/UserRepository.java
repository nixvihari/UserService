package com.spark.lms.userservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spark.lms.userservice.entity.User;

public interface UserRepository extends JpaRepository<User, UUID>	 {

	Optional<User> findByEmail(String email);
}
