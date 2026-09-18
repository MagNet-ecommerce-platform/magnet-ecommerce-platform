package com.magnet.ecommerce.identity_service.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserUnlockResponse(
        UUID userId,
        String email,
        String status,
        boolean enabled,
        int failedLoginAttempts,
        Instant lockedAt,
        String message
) {
}