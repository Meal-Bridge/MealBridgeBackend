package com.meal_bridge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RoleBasedUserDetailsService implements UserDetailsService {

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private MessUserDetailsService messOwnerDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserDetails userDetails = clientDetailsService.loadUserByUsername(username);
            if (userDetails.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("USER"))) {
                return userDetails;
            }
        } catch (UsernameNotFoundException ignored) {
            
        }

        try {
            UserDetails userDetails = messOwnerDetailsService.loadUserByUsername(username);
            if (userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN") || auth.getAuthority().equals("ASSISTANT"))) {
                return userDetails;
            }
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("User not found");
        }

        throw new UsernameNotFoundException("Invalid user role or user not found");
    }
}

