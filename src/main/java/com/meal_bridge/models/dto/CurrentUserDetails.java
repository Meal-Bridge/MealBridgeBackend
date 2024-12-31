package com.meal_bridge.models.dto;

import com.meal_bridge.models.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentUserDetails {
    
    private String username;
    private Role role;
}