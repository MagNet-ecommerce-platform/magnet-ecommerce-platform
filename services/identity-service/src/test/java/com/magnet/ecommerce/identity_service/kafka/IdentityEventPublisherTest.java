package com.magnet.ecommerce.identity_service.kafka;

import com.magnet.ecommerce.identity_service.entity.User;
import com.magnet.ecommerce.identity_service.entity.enums.UserStatus;
import com.magnet.ecommerce.identity_service.event.UserActivatedEvent;
import com.magnet.ecommerce.identity_service.event.UserRegisteredEvent;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class IdentityEventPublisherTest {

    @Test
    void shouldPublishUserRegisteredEventImmediatelyWhenNoTransactionIsActive() {

        KafkaEventPublisher kafkaEventPublisher =
                mock(KafkaEventPublisher.class);

        IdentityEventPublisher publisher =
                new IdentityEventPublisher(kafkaEventPublisher);

        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                "testuser",
                "testuser@example.com",
                "password-hash",
                "Test",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        publisher.publishUserRegistered(user);

        var eventCaptor =
                org.mockito.ArgumentCaptor.forClass(Object.class);

        verify(kafkaEventPublisher).publish(
                eq(KafkaTopicConfig.USER_EVENTS_TOPIC),
                eq(userId.toString()),
                eventCaptor.capture()
        );

        Object capturedEvent = eventCaptor.getValue();

        UserRegisteredEvent event =
                assertInstanceOf(
                        UserRegisteredEvent.class,
                        capturedEvent
                );

        assertEquals(userId, event.userId());
        assertEquals("testuser", event.username());
        assertEquals("testuser@example.com", event.email());
    }

    @Test
    void shouldPublishUserActivatedEventImmediatelyWhenNoTransactionIsActive() {

        KafkaEventPublisher kafkaEventPublisher =
                mock(KafkaEventPublisher.class);

        IdentityEventPublisher publisher =
                new IdentityEventPublisher(kafkaEventPublisher);

        UUID userId = UUID.randomUUID();

        User user = new User(
                userId,
                "activateduser",
                "activateduser@example.com",
                "password-hash",
                "Activated",
                "User",
                UserStatus.ACTIVE,
                true,
                Instant.now(),
                Instant.now()
        );

        publisher.publishUserActivated(user);

        var eventCaptor =
                org.mockito.ArgumentCaptor.forClass(Object.class);

        verify(kafkaEventPublisher).publish(
                eq(KafkaTopicConfig.USER_ACTIVATED_TOPIC),
                eq(userId.toString()),
                eventCaptor.capture()
        );

        Object capturedEvent = eventCaptor.getValue();

        UserActivatedEvent event =
                assertInstanceOf(
                        UserActivatedEvent.class,
                        capturedEvent
                );

        assertEquals(userId, event.userId());
        assertEquals("activateduser", event.username());
        assertEquals("activateduser@example.com", event.email());
    }

    @Test
void shouldPublishUserRegisteredEventOnlyAfterTransactionCommit() {

    KafkaEventPublisher kafkaEventPublisher =
            mock(KafkaEventPublisher.class);

    IdentityEventPublisher publisher =
            new IdentityEventPublisher(kafkaEventPublisher);

    UUID userId = UUID.randomUUID();

    User user = new User(
            userId,
            "transactionuser",
            "transactionuser@example.com",
            "password-hash",
            "Transaction",
            "User",
            UserStatus.ACTIVE,
            true,
            Instant.now(),
            Instant.now()
    );

    TransactionSynchronizationManager.initSynchronization();

    try {
        publisher.publishUserRegistered(user);

        org.mockito.Mockito.verifyNoInteractions(kafkaEventPublisher);

        TransactionSynchronizationManager.getSynchronizations()
                .forEach(TransactionSynchronization::afterCommit);

        org.mockito.Mockito.verify(kafkaEventPublisher).publish(
                eq(KafkaTopicConfig.USER_EVENTS_TOPIC),
                eq(userId.toString()),
                org.mockito.ArgumentMatchers.any(UserRegisteredEvent.class)
        );

    } finally {
        TransactionSynchronizationManager.clearSynchronization();
    }
}
}