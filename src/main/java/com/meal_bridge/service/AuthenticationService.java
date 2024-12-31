package com.meal_bridge.service;

import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.meal_bridge.exception.EmailAlreadyExistsException;
import com.meal_bridge.exception.LoginCredentialWrongException;
import com.meal_bridge.exception.NullValueException;
import com.meal_bridge.models.dto.AuthenticationResponse;
import com.meal_bridge.models.dto.ClientDto;
import com.meal_bridge.models.dto.LoginRequest;
import com.meal_bridge.models.dto.MessUserSignUpDto;
import com.meal_bridge.models.entity.Role;
import com.meal_bridge.models.entity.Client;
import com.meal_bridge.models.entity.MessUser;
import com.meal_bridge.repository.ClientRepository;
import com.meal_bridge.repository.MessUserRepository;

@Service
public class AuthenticationService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MessUserRepository messUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private MessUserDetailsService messUserDetailsService;

    public AuthenticationResponse register(ClientDto request) throws NullValueException, EmailAlreadyExistsException {

        Optional<Client> clientByUser = clientRepository.findByEmailOrPhone(request.getEmail(), request.getPhone());
        if (clientByUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email or Phone already Used");
        }

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setRole(Role.USER);
        client.setDob(request.getDob());
        client.setAge(request.getAge());
        client.setGender(request.getGender());
        client.setPhone(request.getPhone());

        client = clientRepository.save(client);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setMessage("Registration Successful....!!!");
        authenticationResponse.setStatus(true);
        authenticationResponse.setToken(null);

        return authenticationResponse;
    }

    public AuthenticationResponse authenticate(LoginRequest request) throws LoginCredentialWrongException {

        Client client = clientRepository.findByEmail(request.getUsername())
                .orElseThrow(
                        () -> new LoginCredentialWrongException(
                                "Invalid Email or password. Please Enter valid Credentials..!"));

        UserDetails userDetails = clientDetailsService.loadUserByUsername(request.getUsername());

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {

            throw new LoginCredentialWrongException("Invalid Email or password. Please Enter valid Credentials..!");
        }

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        if (authenticate.isAuthenticated()) {
            String token = jwtService.generateToken(request.getUsername(), client.getRole().toString());

            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setMessage("SignIn Successful....!!!");
            authenticationResponse.setStatus(true);
            authenticationResponse.setToken(token);
            authenticationResponse.setTime(LocalTime.now());
            return authenticationResponse;
        } else {
            throw new LoginCredentialWrongException("Invalid Email or password. Please Enter valid Credentials..!"); 
        }
    }

    public MessUser messOwnerRegister(MessUserSignUpDto messUserSignUpDto) throws EmailAlreadyExistsException {
        Optional<MessUser> messUserByUser = messUserRepository.findByEmailOrPhone(messUserSignUpDto.getEmail(), messUserSignUpDto.getPhone());
        if (messUserByUser.isPresent()) {
            throw new EmailAlreadyExistsException("Email or Phone already Used");
        }
        messUserSignUpDto.setPassword(passwordEncoder.encode(messUserSignUpDto.getPassword()));
        MessUser messUser = messUserSignUpDto.toMessUser();
        messUser.setRole(Role.ADMIN);

        return messUserRepository.save(messUser);
    }

    public AuthenticationResponse messOwnerLogin(LoginRequest request) throws LoginCredentialWrongException {

        messUserRepository.findByEmail(request.getUsername())
                .orElseThrow(() -> new LoginCredentialWrongException("Invalid Email or password. Please Enter valid Credentials..!"));
        
        UserDetails userDetails = messUserDetailsService.loadUserByUsername(request.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new LoginCredentialWrongException("Invalid Email or password. Please Enter valid Credentials..!");      
        }

        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if (authenticate.isAuthenticated()) {
            String token = jwtService.generateToken(request.getUsername(), Role.ADMIN.toString());
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setMessage("SignIn Successful....!!!");
            authenticationResponse.setStatus(true);
            authenticationResponse.setToken(token);
            authenticationResponse.setTime(LocalTime.now());
            return authenticationResponse;
        } else {
            throw new LoginCredentialWrongException("Invalid Email or password. Please Enter valid Credentials..!");  
        }
    }
}