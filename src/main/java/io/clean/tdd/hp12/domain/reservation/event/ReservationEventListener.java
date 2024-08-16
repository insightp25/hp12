package io.clean.tdd.hp12.domain.reservation.event;

import io.clean.tdd.hp12.domain.reservation.port.ReservationMessageProducer;
import io.clean.tdd.hp12.domain.reservation.port.ReservationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final ReservationOutboxRepository reservationOutboxRepository;
    private final ReservationMessageProducer reservationMessageProducer;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveReservationOutboxMessage(ReservationCompletionEvent reservationCompletionEvent) {
        reservationOutboxRepository.saveOutOf(reservationCompletionEvent.reservation());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void produceReservationMessage(ReservationCompletionEvent reservationCompletionEvent) {
        reservationMessageProducer.produceReservationMessage(reservationCompletionEvent.reservation());
    }
}