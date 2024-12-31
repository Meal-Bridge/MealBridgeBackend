package com.meal_bridge.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull(message = "Username is required")
    @Email(message = "Username should be valid")
    private String username;

    @NotNull(message = "Password is required")
    private String password;
}
