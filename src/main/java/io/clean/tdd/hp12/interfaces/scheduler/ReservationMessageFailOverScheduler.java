package io.clean.tdd.hp12.interfaces.scheduler;

import io.clean.tdd.hp12.infrastructure.reservation.ReservationOutboxJpaRepository;
import io.clean.tdd.hp12.infrastructure.reservation.enums.ReservationOutboxStatus;
import io.clean.tdd.hp12.infrastructure.reservation.message.ReservationMessageKafkaProducer;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationEntity;
import io.clean.tdd.hp12.infrastructure.reservation.model.ReservationOutboxEntity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationMessageFailOverScheduler {

    private static final int FAIL_OVER_INTERVAL_SECONDS = 30;
    private static final int FAIL_OVER_SCAN_RANGE_SECONDS = 300;

    private final ReservationOutboxJpaRepository reservationOutboxJpaRepository;
    private final ReservationMessageKafkaProducer reservationMessageKafkaProducer;

    @Scheduled(fixedRate = FAIL_OVER_INTERVAL_SECONDS * 1000)
    public void resendFailedReservationMessages() {

        LocalDateTime timestampFrom = LocalDateTime.now().minusSeconds(FAIL_OVER_SCAN_RANGE_SECONDS).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime timestampUntil = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        List<ReservationOutboxEntity> unpublishedReservationMessages =
            reservationOutboxJpaRepository.findAllByCreatedAtBetweenAndStatus(
                timestampFrom,
                timestampUntil,
                ReservationOutboxStatus.CREATED);

        unpublishedReservationMessages.stream()
            .map(ReservationOutboxEntity::getReservationEntity)
            .map(ReservationEntity::toModel)
            .forEach(reservationMessageKafkaProducer::produceReservationMessage);
    }
}