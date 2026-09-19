package com.magnet.ecommerce.identity_service.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserRegisteredEventTest {

    @Test
    void shouldCreateUserRegisteredEvent() {

        UUID userId = UUID.randomUUID();
        Instant occurredAt = Instant.now();

        UserRegisteredEvent event = new UserRegisteredEvent(
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