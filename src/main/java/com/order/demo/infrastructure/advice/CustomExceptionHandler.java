package com.order.demo.infrastructure.advice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.order.demo.domain.exceptions.OrderAlreadyExistsException;
import com.order.demo.domain.exceptions.OrderNotFoundException;
import com.order.demo.infrastructure.rest.responses.ErrorResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String errorMessage = error.getDefaultMessage();
            if (error instanceof FieldError) {
                String fieldName = ((FieldError) error).getField();
                errors.add(fieldName + ": " + errorMessage);
            } else {
                errors.add(errorMessage);
            }
        });
        
        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> errors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            errors.add(violation.getMessage());
        });
        return ResponseEntity.badRequest().body(new ErrorResponse(errors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.of("Invalid UUID format"));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(ex.getMessage()));
    }

    @ExceptionHandler(OrderAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleOrderAlreadyExists(OrderAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse.of(ex.getMessage()));
    }
}