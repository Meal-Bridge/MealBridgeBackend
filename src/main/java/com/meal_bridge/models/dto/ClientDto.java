package com.meal_bridge.models.dto;

import java.time.LocalDate;

import com.meal_bridge.models.enums.Gender;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDto {
    private Long id;

    @NotNull(message = "First name is required")
    private String firstName;

    private String middleName;
    
    @NotNull(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Email is required")
    @Pattern(
        regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", 
        message = "Email should be valid"
    )   
    private String email;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Phone is required")
    @Pattern(
        regexp = "^\\+?[0-9]{10,15}$", 
        message = "Phone number must be valid and between 10 to 15 digits"
    )
    private String phone;

    private LocalDate dob;
    
    private Integer age;
    
    @NotNull(message = "Gender is required")
    private Gender gender;

    private String salutation;
}
