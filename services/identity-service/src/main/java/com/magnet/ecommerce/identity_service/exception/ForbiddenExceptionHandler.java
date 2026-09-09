package com.magnet.ecommerce.identity_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ForbiddenExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAuthorizationDenied(
            AuthorizationDeniedException exception,
            HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "FORBIDDEN");
        body.put(
                "message",
                "You do not have permission to access this resource"
        );
        body.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(body);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<Map<String, Object>>
    handleAuthenticationCredentialsNotFound(
            AuthenticationCredentialsNotFoundException exception,
            HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "UNAUTHORIZED");
        body.put(
                "message",
                "Authentication is required to access this resource"
        );
        body.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidRefreshToken(
            InvalidRefreshTokenException exception,
            HttpServletRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "UNAUTHORIZED");
        body.put("message", exception.getMessage());
        body.put("path", request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }
}