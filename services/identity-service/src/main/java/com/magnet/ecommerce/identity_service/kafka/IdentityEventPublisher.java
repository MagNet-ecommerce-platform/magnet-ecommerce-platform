package com.magnet.ecommerce.identity_service.kafka;

import com.magnet.ecommerce.identity_service.event.UserRegisteredEvent;
import com.magnet.ecommerce.identity_service.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import com.magnet.ecommerce.identity_service.event.UserActivatedEvent;

import java.time.Instant;

@Service
public class IdentityEventPublisher {

    private final KafkaEventPublisher kafkaEventPublisher;

    public IdentityEventPublisher(KafkaEventPublisher kafkaEventPublisher) {
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    public void publishUserRegistered(User user) {

        UserRegisteredEvent event = new UserRegisteredEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                Instant.now()
        );

        Runnable publishAction = () ->
                kafkaEventPublisher.publish(
                        KafkaTopicConfig.USER_EVENTS_TOPIC,
                        user.getId().toString(),
                        event
                );

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            publishAction.run();
                        }
                    }
            );
        } else {
            publishAction.run();
        }
    }

    public void publishUserActivated(User user) {

        UserActivatedEvent event = new UserActivatedEvent(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                Instant.now()
        );
    
        Runnable publishAction = () ->
                kafkaEventPublisher.publish(
                        KafkaTopicConfig.USER_ACTIVATED_TOPIC,
                        user.getId().toString(),
                        event
                );
    
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            publishAction.run();
                        }
                    }
            );
        } else {
            publishAction.run();
        }
    }
}