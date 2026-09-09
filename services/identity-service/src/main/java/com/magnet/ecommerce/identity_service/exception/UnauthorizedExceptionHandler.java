package com.magnet.ecommerce.identity_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class UnauthorizedExceptionHandler {

    public ResponseEntity<Map<String, Object>> handleUnauthorized(
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
}