package com.meal_bridge.utils;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.meal_bridge.models.dto.CurrentUserDetails;
import com.meal_bridge.models.entity.Role;

public class JsonWebTokenUtil {

    public static CurrentUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authority = null;
        String name = null;
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            Optional<? extends GrantedAuthority> first = authorities.stream().findFirst();
            if (first.isPresent()) {
                authority = first.get().getAuthority();
            }

            name = authentication.getName();
        }
        return new CurrentUserDetails(name, Role.valueOf(authority));
    }
}