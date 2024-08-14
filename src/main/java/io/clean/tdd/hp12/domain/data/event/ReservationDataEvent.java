package io.clean.tdd.hp12.domain.data.event;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;

public record ReservationDataEvent(
    Reservation reservation
) {

}
