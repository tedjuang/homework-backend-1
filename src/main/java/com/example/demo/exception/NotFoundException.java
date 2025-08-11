package com.example.demo.exception;

// Custom exception for not found resources
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
