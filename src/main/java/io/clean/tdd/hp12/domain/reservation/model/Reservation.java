package io.clean.tdd.hp12.domain.reservation.model;

import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Reservation(
    long id,
    ReservationStatus status,
    LocalDateTime createdAt,
    Seat seat,
    User user
) {
}
