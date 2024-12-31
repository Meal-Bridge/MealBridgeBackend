package com.meal_bridge.controller;

import org.springframework.web.bind.annotation.RestController;

import com.meal_bridge.exception.EmailAlreadyExistsException;
import com.meal_bridge.exception.LoginCredentialWrongException;
import com.meal_bridge.exception.NullValueException;
import com.meal_bridge.models.dto.AuthenticationResponse;
import com.meal_bridge.models.dto.ClientDto;
import com.meal_bridge.models.dto.LoginRequest;
import com.meal_bridge.models.dto.MessUserSignUpDto;
import com.meal_bridge.service.AuthenticationService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.meal_bridge.utils.ControllerConstants.REGISTER_CLIENT;
import static com.meal_bridge.utils.ControllerConstants.LOGIN_CLIENT;
import static com.meal_bridge.utils.ControllerConstants.REGISTER_MESS_USER;
import static com.meal_bridge.utils.ControllerConstants.LOGIN_MESS_USER;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(REGISTER_CLIENT)
    public ResponseEntity<AuthenticationResponse> registerUser(@Valid @RequestBody ClientDto request) throws NullValueException, EmailAlreadyExistsException {

        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping(LOGIN_CLIENT)
    public ResponseEntity<AuthenticationResponse> loginUser(@Valid @RequestBody LoginRequest request) throws LoginCredentialWrongException {
       
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping(REGISTER_MESS_USER)
    public ResponseEntity<?> messOwnerRegister(@Valid @RequestBody MessUserSignUpDto messUserSignUpDto) throws EmailAlreadyExistsException  {
        
        return ResponseEntity.ok(authenticationService.messOwnerRegister(messUserSignUpDto));
    }

    @PostMapping(LOGIN_MESS_USER)
    public ResponseEntity<AuthenticationResponse> messOwnerLogin(@Valid @RequestBody LoginRequest loginRequest) throws LoginCredentialWrongException {
        
        return ResponseEntity.ok(authenticationService.messOwnerLogin(loginRequest));
    } 
}
