package com.magnet.ecommerce.identity_service.event;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String username,
        String email,
        Instant occurredAt
) {
}