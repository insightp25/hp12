package io.clean.tdd.hp12.domain.reservation.port;

import io.clean.tdd.hp12.domain.reservation.model.Reservation;

public interface ReservationMessageProducer {

    void produceReservationMessage(Reservation reservation);
}