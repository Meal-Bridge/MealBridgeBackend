package com.meal_bridge.models.dto;

import com.meal_bridge.models.entity.MessUser;
import com.meal_bridge.models.enums.Gender;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessUserSignUpDto {

    @NotNull(message = "First name is required")
    private String firstName;

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

    @NotNull(message="Gender is required")
    private Gender gender;

    public MessUser toMessUser() {
        MessUser messUser = new MessUser();
        messUser.setFirstName(this.firstName);
        messUser.setLastName(this.lastName);
        messUser.setEmail(this.email);
        messUser.setPassword(this.password);
        messUser.setPhone(this.phone);
        messUser.setGender(this.gender);
        if (this.gender == Gender.MALE) {
            messUser.setSalutation("Mr.");
        } else if (this.gender == Gender.FEMALE) {
            messUser.setSalutation("Ms.");
        } else {
            messUser.setSalutation("Mx.");
        }
        return messUser;
    }
}