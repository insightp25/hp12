package io.clean.tdd.hp12.configuration;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

//@Configuration // 부하 테스트를 위한 임시 비활성화
public class KafkaConsumerConfiguration {

    //@Bean // 부하 테스트를 위한 임시 비활성화
    public ConsumerFactory<Long, Reservation> reservationConsumerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-id-1");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "io.clean.tdd.hp12.domain.reservation.model");

        return new DefaultKafkaConsumerFactory<>(properties, new LongDeserializer(), new JsonDeserializer<>(Reservation.class));
    }

    //@Bean // 부하 테스트를 위한 임시 비활성화
    public ConcurrentKafkaListenerContainerFactory<Long, Reservation> reservationListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, Reservation> reservationListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        reservationListenerContainerFactory.setConsumerFactory(reservationConsumerFactory());

        return reservationListenerContainerFactory;
    }

    //@Bean // 부하 테스트를 위한 임시 비활성화
    public ConsumerFactory<Long, Reservation> reservationConsumerFactory2() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-id-2");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        properties.put(JsonDeserializer.TRUSTED_PACKAGES, "io.clean.tdd.hp12.domain.reservation.model");

        return new DefaultKafkaConsumerFactory<>(properties, new LongDeserializer(), new JsonDeserializer<>(Reservation.class));
    }

    //@Bean // 부하 테스트를 위한 임시 비활성화
    public ConcurrentKafkaListenerContainerFactory<Long, Reservation> reservationListenerContainerFactory2() {
        ConcurrentKafkaListenerContainerFactory<Long, Reservation> reservationListenerContainerFactory2 = new ConcurrentKafkaListenerContainerFactory<>();
        reservationListenerContainerFactory2.setConsumerFactory(reservationConsumerFactory2());

        return reservationListenerContainerFactory2;
    }
}
