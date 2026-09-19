package com.magnet.ecommerce.identity_service.kafka;

import com.magnet.ecommerce.identity_service.event.UserActivatedEvent;
import com.magnet.ecommerce.identity_service.event.UserRegisteredEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class KafkaEventConsumerTest {

    @Test
    void shouldConsumeUserRegisteredEvent() {

        KafkaEventConsumer consumer = new KafkaEventConsumer();

        UserRegisteredEvent event = new UserRegisteredEvent(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                Instant.now()
        );

        assertDoesNotThrow(() ->
                consumer.consumeUserRegisteredEvent(event)
        );
    }

    @Test
    void shouldConsumeUserActivatedEvent() {

        KafkaEventConsumer consumer = new KafkaEventConsumer();

        UserActivatedEvent event = new UserActivatedEvent(
                UUID.randomUUID(),
                "testuser",
                "testuser@example.com",
                Instant.now()
        );

        assertDoesNotThrow(() ->
                consumer.consumeUserActivatedEvent(event)
        );
    }
}