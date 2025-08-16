package com.meal_bridge.exception;

public class ValidationFailException extends Exception{

    public ValidationFailException() {}

    public ValidationFailException(String message) {
        super(message);
    }
}
