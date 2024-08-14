package io.clean.tdd.hp12.domain.reservation.event;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;

public record ReservationCompletionEvent(
    Reservation reservation
) {

}
