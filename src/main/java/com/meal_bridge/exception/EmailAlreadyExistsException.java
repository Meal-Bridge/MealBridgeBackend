package com.meal_bridge.exception;

public class EmailAlreadyExistsException extends Exception {
    
    public EmailAlreadyExistsException() {}

    public EmailAlreadyExistsException(String message) {

        super(message);
    }
}
