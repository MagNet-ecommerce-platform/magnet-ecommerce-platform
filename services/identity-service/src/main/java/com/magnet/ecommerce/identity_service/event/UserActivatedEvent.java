package com.magnet.ecommerce.identity_service.event;

import java.time.Instant;
import java.util.UUID;

public record UserActivatedEvent(
        UUID userId,
        String username,
        String email,
        Instant occurredAt
) {
}