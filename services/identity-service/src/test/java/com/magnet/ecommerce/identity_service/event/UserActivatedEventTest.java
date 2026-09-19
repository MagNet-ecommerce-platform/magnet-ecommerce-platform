package com.magnet.ecommerce.identity_service.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserActivatedEventTest {

    @Test
    void shouldCreateUserActivatedEvent() {

        UUID userId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        UserActivatedEvent event = new UserActivatedEvent(
                userId,
                "testuser",
                "testuser@example.com",
                occurredAt
        );

        assertNotNull(event);
        assertEquals(userId, event.userId());
        assertEquals("testuser", event.username());
        assertEquals("testuser@example.com", event.email());
        assertEquals(occurredAt, event.occurredAt());
    }
}