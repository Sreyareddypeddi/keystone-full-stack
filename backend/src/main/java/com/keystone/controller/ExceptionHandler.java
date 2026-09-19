package com.keystone.controller;

import com.keystone.dto.Dtos;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Dtos.ErrorResponse> validation(MethodArgumentNotValidException e) {
        Map<String, String> f = new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(x -> f.put(x.getField(), x.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(new Dtos.ErrorResponse(
                        LocalDateTime.now(),
                        400,
                        "Validation failed",
                        f
                ));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalStateException.class)
    ResponseEntity<Dtos.ErrorResponse> conflict(IllegalStateException e) {
        return ResponseEntity.status(409)
                .body(new Dtos.ErrorResponse(
                        LocalDateTime.now(),
                        409,
                        e.getMessage(),
                        Map.of()
                ));
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    ResponseEntity<Dtos.ErrorResponse> runtime(RuntimeException e) {
        int s = e.getMessage() != null &&
                e.getMessage().equals("Forbidden") ? 403 : 400;

        return ResponseEntity.status(s)
                .body(new Dtos.ErrorResponse(
                        LocalDateTime.now(),
                        s,
                        e.getMessage(),
                        Map.of()
                ));
    }
}