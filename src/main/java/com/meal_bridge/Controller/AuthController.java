package com.meal_bridge.Controller;

import com.meal_bridge.exception.ValidationFailException;
import com.meal_bridge.model.dto.AuthRequest;
import com.meal_bridge.model.dto.AuthResponse;
import com.meal_bridge.model.dto.RefreshTokenRequest;
import com.meal_bridge.model.dto.RegisterRequest;
import com.meal_bridge.model.entity.User;
import com.meal_bridge.model.enums.Role;
import com.meal_bridge.service.AuthService;
import com.meal_bridge.service.JwtService;
import com.meal_bridge.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User registration attempt for email: {}", request.getEmail());

        try {
            AuthResponse response = authService.register(request);
            log.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (ValidationFailException e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            AuthResponse response = authService.authenticate(request);
            log.info("User logged in successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (ValidationFailException e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            userDetailsService.handleFailedLogin(request.getEmail());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh attempt");

        try {
            AuthResponse response = authService.refreshToken(request.getRefreshToken());
            log.info("Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(user);
    }

    @PutMapping("/roles")
    @PreAuthorize("hasRole('ADMIN') ")
    public ResponseEntity<String> updateUserRoles(
            @RequestParam String email,
            @RequestParam Set<Role> roles,
            @AuthenticationPrincipal User currentUser) {

        log.info("Role update request for user: {} by admin: {}", email, currentUser.getEmail());

        try {
            authService.updateUserRoles(email, roles, currentUser);
            log.info("Roles updated successfully for user: {}", email);
            return ResponseEntity.ok("Roles updated successfully");
        } catch (Exception e) {
            log.error("Failed to update roles for user: {}", email, e);
            return ResponseEntity.badRequest().body("Failed to update roles");
        }
    }

    @PostMapping("/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unlockAccount(@RequestParam String email) {
        log.info("Account unlock request for user: {}", email);

        try {
            userDetailsService.unlockUserAccount(email);
            return ResponseEntity.ok("Account unlocked successfully");
        } catch (Exception e) {
            log.error("Failed to unlock account for user: {}", email, e);
            return ResponseEntity.badRequest().body("Failed to unlock account");
        }
    }

    @PostMapping("/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> disableAccount(@RequestParam String email) {
        log.info("Account disable request for user: {}", email);

        try {
            userDetailsService.disableUserAccount(email);
            return ResponseEntity.ok("Account disabled successfully");
        } catch (Exception e) {
            log.error("Failed to disable account for user: {}", email, e);
            return ResponseEntity.badRequest().body("Failed to disable account");
        }
    }
}