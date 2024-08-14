package io.clean.tdd.hp12.domain.data.event;

import io.clean.tdd.hp12.domain.data.DataPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataPlatformEventListener {

    private final DataPlatformService dataPlatformService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReservationData(ReservationDataEvent reservationDataEvent) {
        try {
            dataPlatformService.sendReservationData(reservationDataEvent.reservation());
        } catch (Exception e) {
            log.debug("Error has occurred during async operation: handleReservationData(), cause={}, message={}", e.getCause(), e.getMessage());
        }
    }
}
