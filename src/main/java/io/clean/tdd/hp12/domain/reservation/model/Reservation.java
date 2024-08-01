package io.clean.tdd.hp12.domain.reservation.model;

import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.reservation.enums.ReservationStatus;
import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record Reservation(
    long id,
    ReservationStatus status,
    LocalDateTime createdAt,
    Seat seat,
    User user,
    Payment payment
) {
    public static List<Reservation> hold(List<Seat> seats, User user, Payment payment) {
        return seats.stream()
            .map(seat -> Reservation.builder()
                .status(ReservationStatus.ON_HOLD)
                .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
                .seat(seat)
                .user(user)
                .payment(payment)
                .build())
            .collect(Collectors.toList());
    }
}
