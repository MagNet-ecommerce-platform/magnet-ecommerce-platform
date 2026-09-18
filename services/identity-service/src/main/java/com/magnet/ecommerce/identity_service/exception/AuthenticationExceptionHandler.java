package com.magnet.ecommerce.identity_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class AuthenticationExceptionHandler {

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<AuthenticationErrorResponse> handleInvalidLogin(
            InvalidLoginException exception,
            HttpServletRequest request) {

        AuthenticationErrorResponse response =
                new AuthenticationErrorResponse(
                        Instant.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        "UNAUTHORIZED",
                        exception.getMessage(),
                        request.getRequestURI(),
                        exception.getFailedAttempts(),
                        exception.getAttemptsRemaining()
                );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<AuthenticationErrorResponse> handleAccountLocked(
            AccountLockedException exception,
            HttpServletRequest request) {

        AuthenticationErrorResponse response =
                new AuthenticationErrorResponse(
                        Instant.now(),
                        HttpStatus.LOCKED.value(),
                        "LOCKED",
                        exception.getMessage(),
                        request.getRequestURI(),
                        5,
                        0
                );

        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(response);
    }
}