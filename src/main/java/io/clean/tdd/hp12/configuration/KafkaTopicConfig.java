package io.clean.tdd.hp12.configuration;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

//@Configuration // 부하 테스트를 위한 임시 비활성화
public class KafkaTopicConfig {

    //@Bean // 부하 테스트를 위한 임시 비활성화
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "broker:9092");

        return new KafkaAdmin(configurations);
    }

    //@Bean // 부하 테스트를 위한 임시 비활성화
    public NewTopic reservationTopic() {
        return TopicBuilder
            .name("reservations")
            .partitions(3)
            .build();
    }
}
