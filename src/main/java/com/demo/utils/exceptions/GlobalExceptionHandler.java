package com.demo.utils.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlayerServiceException.class)
    public ResponseEntity<RuntimeErrorResponse> handlePlayerServiceException(PlayerServiceException ex, HttpServletRequest request) {
        RuntimeErrorResponse runtimeErrorResponse = new RuntimeErrorResponse(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(runtimeErrorResponse);
    }

    @ExceptionHandler(ProductsServiceException.class)
    public ResponseEntity<RuntimeErrorResponse> handlePProductsServiceException(ProductsServiceException ex, HttpServletRequest request) {
        RuntimeErrorResponse runtimeErrorResponse = new RuntimeErrorResponse(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(runtimeErrorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RuntimeErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        RuntimeErrorResponse runtimeErrorResponse = new RuntimeErrorResponse(
                request.getRequestURI(),
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(runtimeErrorResponse);
    }
}
