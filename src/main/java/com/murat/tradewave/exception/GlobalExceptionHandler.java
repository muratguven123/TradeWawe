package com.murat.tradewave.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("time stap", String.valueOf(LocalDateTime.now()));
        errors.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        errors.put("path", request.getRequestURI());

        Map<String,String> validationErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.put(error.getField(), error.getDefaultMessage());
        }
        errors.put("error",validationErrors);
        errors.put("errors","validation failed");

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);


    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(final Exception ex, final HttpServletRequest request) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("time stap", String.valueOf(LocalDateTime.now()));
        errors.put("status", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        errors.put("message", ex.getMessage());
        errors.put("path", request.getRequestURI());

        return new ResponseEntity<>(errors, HttpStatus.INTERNAL_SERVER_ERROR);

    }








}
