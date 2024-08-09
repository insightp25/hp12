package io.clean.tdd.hp12.domain.reservation.port;

import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.reservation.model.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository {
    Reservation save(Reservation reservation);

    List<Reservation> findByPaymentId(long paymentId);

    List<Reservation> findAllByStatusAndCreatedAtLessThanEqual(LocalDateTime abolishTimestampFrom,
        LocalDateTime abolishTimestampUntil, ReservationStatus status);
}
