package io.clean.tdd.hp12.message;

import io.clean.tdd.hp12.domain.data.DataPlatformService;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationKafkaListener {

    private final DataPlatformService dataPlatformService;

    @KafkaListener(topics = "reservations", groupId = "test-group-id-1", containerFactory = "reservationListenerContainerFactory")
    public void listen(Reservation reservation) {
        dataPlatformService.sendReservationData(reservation);
    }
}
