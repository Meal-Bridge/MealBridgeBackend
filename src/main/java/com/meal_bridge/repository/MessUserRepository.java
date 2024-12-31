package com.meal_bridge.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.meal_bridge.models.entity.MessUser;

public interface MessUserRepository extends JpaRepository<MessUser, Long> {
    
    Optional<MessUser> findByEmail(String email);

    Optional<MessUser> findByEmailOrPhone(String email, String phone);
}
