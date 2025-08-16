package com.meal_bridge.model.dto;

import com.meal_bridge.model.entity.User;
import com.meal_bridge.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be valid and between 10 to 15 digits"
    )
    private String phone;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters long and contain a letter, a number, and a special character"
    )
    private String password;

    private String firstName;
    private String middleName;
    private String lastName;
    private Gender gender;
    private Long dob;
    private boolean isOwner;
}