package io.clean.tdd.hp12.infrastructure.reservation.message;

import io.clean.tdd.hp12.common.MessageTopics;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.domain.reservation.port.ReservationMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

//@Component // 부하 테스트를 위한 임시 비활성화
@RequiredArgsConstructor
public class ReservationMessageKafkaProducer implements ReservationMessageProducer {

    private final KafkaTemplate<Long, Reservation> reservationKafkaTemplate;

    @Override
    public void produceReservationMessage(Reservation reservation) {
        reservationKafkaTemplate.send(MessageTopics.RESERVATIONS, reservation.id(), reservation);
    }
}
