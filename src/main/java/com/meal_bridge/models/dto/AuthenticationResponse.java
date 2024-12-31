package com.meal_bridge.models.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;

    private String message;

    private boolean status;

    private Object data;

    private LocalTime time;
}
