package io.clean.tdd.hp12.message;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import io.clean.tdd.hp12.infrastructure.reservation.ReservationOutboxJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationOutboxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationOutboxKafkaConsumer {

    private final ReservationOutboxJpaRepository reservationOutboxJpaRepository;

    @KafkaListener(topics = "reservations", groupId = "test-group-id-2", containerFactory = "reservationListenerContainerFactory2")
    public void listen(Reservation reservation) {
        ReservationOutboxEntity reservationOutboxEntity = reservationOutboxJpaRepository.findByReservationEntity_Id(reservation.id());
        reservationOutboxJpaRepository.save(reservationOutboxEntity.toPublishedStatus());
    }
}
