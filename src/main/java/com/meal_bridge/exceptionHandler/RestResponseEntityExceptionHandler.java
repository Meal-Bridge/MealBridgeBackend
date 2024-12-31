package com.meal_bridge.exceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.meal_bridge.exception.EmailAlreadyExistsException;
import com.meal_bridge.exception.LoginCredentialWrongException;
import com.meal_bridge.exception.NullValueException;
import com.meal_bridge.exception.UserNoFoundByEmailException;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@ResponseStatus
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorMessage> noSuchSupplierException(EmailAlreadyExistsException exception,
            WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.CONFLICT, exception.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }

    @ExceptionHandler(NullValueException.class)
    public ResponseEntity<ErrorMessage> noSuchSupplierException(NullValueException exception,
            WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(LoginCredentialWrongException.class)
    public ResponseEntity<ErrorMessage> noSuchSupplierException(LoginCredentialWrongException exception,
            WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.UNAUTHORIZED, exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }

    @ExceptionHandler(UserNoFoundByEmailException.class)
    public ResponseEntity<ErrorMessage> noSuchSupplierException(UserNoFoundByEmailException exception,
            WebRequest request) {
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
                List<String> errorList = ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        ErrorMessage errorDetails = new ErrorMessage(HttpStatus.BAD_REQUEST, errorList.toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGlobalException(Exception exception, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred: " + exception.getMessage());

        exception.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }
}