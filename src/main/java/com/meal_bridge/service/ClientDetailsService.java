package com.meal_bridge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.meal_bridge.repository.ClientRepository;

@Service
public class ClientDetailsService  {

    @Autowired
    private ClientRepository clientRepository;
    
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        return clientRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Email Not Found"));
    }
    
}
