package com.meal_bridge.service;

import com.meal_bridge.exception.ValidationFailException;
import com.meal_bridge.model.dto.AuthRequest;
import com.meal_bridge.model.dto.AuthResponse;
import com.meal_bridge.model.dto.RegisterRequest;
import com.meal_bridge.model.entity.User;
import com.meal_bridge.model.enums.Role;
import com.meal_bridge.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request) throws ValidationFailException {
        log.info("Registering new user: {}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationFailException("User already exists with email: " + request.getEmail());
        }

        if (!isValidPassword(request.getPassword())) {
            throw new ValidationFailException("Password does not meet security requirements");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setDob(request.getDob());

        // Set default role
        Set<Role> defaultRoles = new HashSet<>();
        if (request.isOwner()) {
            defaultRoles.add(Role.ADMIN);
        } else {
            defaultRoles.add(Role.CLIENT);
        }
        user.setRoles(defaultRoles);

        // Save user
        User savedUser = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtService.generateToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        log.info("User registered successfully: {}", request.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(savedUser)
                .build();
    }

    @Transactional
    public AuthResponse authenticate(AuthRequest request) throws ValidationFailException {
        log.info("Authenticating user: {}", request.getEmail());

        try {
            // Check if user exists and is not locked
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ValidationFailException("User not found"));

            if (!user.isAccountNonLocked()) {
                throw new ValidationFailException("Account is locked. Please contact support.");
            }

            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Generate tokens
            String accessToken = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            // Update last login
            user.setLastLoginAt(LocalDateTime.now());
            user.resetFailedLoginAttempts();
            userRepository.save(user);

            log.info("User authenticated successfully: {}", request.getEmail());

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(user)
                    .build();

        } catch (AuthenticationException | ValidationFailException e) {
            log.error("Authentication failed for user: {}", request.getEmail(), e);
            userDetailsService.handleFailedLogin(request.getEmail());
            throw new ValidationFailException("Invalid credentials");
        }
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");

        try {
            String email = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (!jwtService.isValidRefreshToken(refreshToken, user)) {
                throw new RuntimeException("Invalid refresh token");
            }

            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            log.info("Token refreshed successfully for user: {}", email);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .user(user)
                    .build();

        } catch (Exception e) {
            log.error("Token refresh failed", e);
            throw new RuntimeException("Token refresh failed");
        }
    }

    @Transactional
    public void updateUserRoles(String email, Set<Role> roles, User currentUser) {
        log.info("Updating roles for user: {} by admin: {}", email, currentUser.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setRoles(roles);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Roles updated successfully for user: {}", email);
    }


    private boolean isValidPassword(String password) {
        // Password must be at least 8 characters long
        if (password.length() < 8) {
            return false;
        }

        // Must contain at least one letter, one number, and one special character
        boolean hasLetter = password.matches(".*[A-Za-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[@$!%*#?&].*");

        return hasLetter && hasNumber && hasSpecialChar;
    }
}