package com.meal_bridge.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.meal_bridge.models.entity.Client;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmail(String email);

    Optional<Client> findByEmailOrPhone(String email, String phone);
}
