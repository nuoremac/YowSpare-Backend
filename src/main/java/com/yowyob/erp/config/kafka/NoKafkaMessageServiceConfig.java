package com.yowyob.erp.config.kafka;

import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "false")
public class NoKafkaMessageServiceConfig {

    @Bean
    @Primary
    public KafkaMessageService noKafkaMessageService() {
        return new KafkaMessageService(null) {
            @Override
            public Mono<Void> sendMessage(String topic, String key, Object payload, String eventType, UUID tenantId) {
                return Mono.empty();
            }
        };
    }
}
