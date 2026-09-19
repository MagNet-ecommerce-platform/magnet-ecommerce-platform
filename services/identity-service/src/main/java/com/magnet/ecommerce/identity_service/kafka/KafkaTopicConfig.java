package com.magnet.ecommerce.identity_service.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    public static final String USER_EVENTS_TOPIC = "identity.user.events";
    public static final String USER_ACTIVATED_TOPIC = "identity.user.activated";
    
    @Bean
    public NewTopic userEventsTopic() {
        return new NewTopic(USER_EVENTS_TOPIC, 1, (short) 1);
    }
    
    @Bean
    public NewTopic userActivatedTopic() {
        return new NewTopic(USER_ACTIVATED_TOPIC, 1, (short) 1);
    }
}