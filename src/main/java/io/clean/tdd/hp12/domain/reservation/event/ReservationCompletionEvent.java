package io.clean.tdd.hp12.domain.reservation.event;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;
import lombok.Builder;

@Builder
public record ReservationCompletionEvent(
    Reservation reservation
) {

}
