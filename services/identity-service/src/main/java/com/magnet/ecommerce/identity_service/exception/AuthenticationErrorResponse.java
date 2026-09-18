package com.magnet.ecommerce.identity_service.exception;

import java.time.Instant;

public record AuthenticationErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Integer failedAttempts,
        Integer attemptsRemaining
) {
}