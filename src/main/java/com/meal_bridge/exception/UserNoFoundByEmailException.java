package com.meal_bridge.exception;

public class UserNoFoundByEmailException extends Exception {
    
    public UserNoFoundByEmailException() {}

    public UserNoFoundByEmailException(String message) {

        super(message);
    } 
}
