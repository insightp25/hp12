package io.clean.tdd.hp12.message;

import io.clean.tdd.hp12.domain.data.DataPlatformService;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

//@Component // 부하 테스트를 위한 임시 비활성화
@RequiredArgsConstructor
public class DataPlatformKafkaConsumer {

    private final DataPlatformService dataPlatformService;

    @KafkaListener(topics = "reservations", groupId = "test-group-id-1", containerFactory = "reservationListenerContainerFactory")
    public void listen(Reservation reservation) {
        dataPlatformService.sendReservationData(reservation);
    }
}
