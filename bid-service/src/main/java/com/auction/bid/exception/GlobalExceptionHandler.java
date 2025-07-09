package com.auction.bid.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<?> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
        Map<String, Object> errorDetails = Map.of(
                "timestamp", LocalDateTime.now(),
                "error", "BUSINESS_RULE_VIOLATION",
                "message", ex.getMessage(),
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> errorDetails = Map.of(
                "timestamp", LocalDateTime.now(),
                "error", "RESOURCE_NOT_FOUND",
                "message", ex.getMessage(),
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        Map<String, Object> errorDetails = Map.of(
                "timestamp", LocalDateTime.now(),
                "error", "INTERNAL_SERVER_ERROR",
                "message", "Error interno del servidor: " + ex.getMessage(),
                "path", request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}