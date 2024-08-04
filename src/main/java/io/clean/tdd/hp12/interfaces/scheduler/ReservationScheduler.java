package io.clean.tdd.hp12.interfaces.scheduler;

import io.clean.tdd.hp12.domain.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationService reservationService;


    @Scheduled(fixedRate = 3 * 1000)
    public void expireTimedOutReservation() {
        reservationService.bulkExpireTimedOutReservations();
    }
}
