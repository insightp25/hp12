package io.clean.tdd.hp12.configuration;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

//@Configuration // 부하 테스트를 위한 임시 비활성화
public class KafkaProducerConfiguration {

    //@Bean // 부하 테스트를 위한 임시 비활성화
    public ProducerFactory<Long, Reservation> reservationProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    //@Bean // 부하 테스트를 위한 임시 비활성화
    public KafkaTemplate<Long, Reservation> reservationKafkaTemplate() {
        return new KafkaTemplate<>(reservationProducerFactory());
    }
}
