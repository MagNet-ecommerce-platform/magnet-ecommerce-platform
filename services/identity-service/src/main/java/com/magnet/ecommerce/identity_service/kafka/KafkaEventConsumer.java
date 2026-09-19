package com.magnet.ecommerce.identity_service.kafka;

import com.magnet.ecommerce.identity_service.event.UserActivatedEvent;
import com.magnet.ecommerce.identity_service.event.UserRegisteredEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaEventConsumer {

    @KafkaListener(
            topics = KafkaTopicConfig.USER_EVENTS_TOPIC,
            groupId = "identity-service-events"
    )
    public void consumeUserRegisteredEvent(UserRegisteredEvent event) {
        processUserRegisteredEvent(event);
    }

    @KafkaListener(
            topics = KafkaTopicConfig.USER_ACTIVATED_TOPIC,
            groupId = "identity-service-activated"
    )
    public void consumeUserActivatedEvent(UserActivatedEvent event) {
        processUserActivatedEvent(event);
    }

    void processUserRegisteredEvent(UserRegisteredEvent event) {
        System.out.println(
                "Received UserRegisteredEvent: " + event
        );
    }

    void processUserActivatedEvent(UserActivatedEvent event) {
        System.out.println(
                "Received UserActivatedEvent: " + event
        );
    }
}