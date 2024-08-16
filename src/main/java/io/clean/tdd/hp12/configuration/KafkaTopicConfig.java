package io.clean.tdd.hp12.configuration;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        return new KafkaAdmin(configurations);
    }

    @Bean
    public NewTopic reservationTopic() {
        return TopicBuilder
            .name("reservations")
            .partitions(3)
            .build();
    }

    @Bean
    public NewTopic reservationOutboxTopic() {
        return TopicBuilder
            .name("reservation-outbox")
            .partitions(3)
            .build();
    }
}
