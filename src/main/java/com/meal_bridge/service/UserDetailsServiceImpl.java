package com.meal_bridge.service;

import com.meal_bridge.model.entity.User;
import com.meal_bridge.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j // CHANGED: Added logging for security events
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // CHANGED: Added transaction for consistency
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username); // CHANGED: Added debug logging

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", username); // CHANGED: Added warning log
                    return new UsernameNotFoundException("User not found with email: " + username);
                });

        if (!user.isEnabled()) {
            log.warn("User account is disabled: {}", username);
            throw new UsernameNotFoundException("User account is disabled");
        }

        if (!user.isAccountNonLocked()) {
            log.warn("User account is locked: {}", username);
            throw new UsernameNotFoundException("User account is locked");
        }

        if (!user.isAccountNonExpired()) {
            log.warn("User account is expired: {}", username);
            throw new UsernameNotFoundException("User account is expired");
        }

        if (!user.isCredentialsNonExpired()) {
            log.warn("User credentials are expired: {}", username);
            throw new UsernameNotFoundException("User credentials are expired");
        }

        log.debug("User loaded successfully: {} with roles: {}", username, user.getRoles());
        return user;
    }

    @Transactional
    public void updateLastLogin(String username) {
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            user.setLastLoginAt(LocalDateTime.now());
            user.resetFailedLoginAttempts(); // Reset failed attempts on successful login
            userRepository.save(user);

            log.info("Updated last login time for user: {}", username);
        } catch (Exception e) {
            log.error("Error updating last login time for user: {}", username, e);
        }
    }

    @Transactional
    public void handleFailedLogin(String username) {
        try {
            User user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                user.incrementFailedLoginAttempts();
                userRepository.save(user);

                log.warn("Failed login attempt for user: {}. Total attempts: {}",
                        username, user.getFailedLoginAttempts());
            }
        } catch (Exception e) {
            log.error("Error handling failed login for user: {}", username, e);
        }
    }

    @Transactional
    public void unlockUserAccount(String username) {
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            user.setAccountNonLocked(true);
            user.resetFailedLoginAttempts();
            userRepository.save(user);

            log.info("User account unlocked: {}", username);
        } catch (Exception e) {
            log.error("Error unlocking user account: {}", username, e);
        }
    }

    @Transactional
    public void disableUserAccount(String username) {
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            user.setEnabled(false);
            userRepository.save(user);

            log.info("User account disabled: {}", username);
        } catch (Exception e) {
            log.error("Error disabling user account: {}", username, e);
        }
    }

    @Transactional
    public void enableUserAccount(String username) {
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            user.setEnabled(true);
            userRepository.save(user);

            log.info("User account enabled: {}", username);
        } catch (Exception e) {
            log.error("Error enabling user account: {}", username, e);
        }
    }

    @Transactional(readOnly = true)
    public boolean userExists(String username) {
        return userRepository.findByEmail(username).isPresent();
    }
}