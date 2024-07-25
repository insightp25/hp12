package io.clean.tdd.hp12.domain.concert.model;

import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import lombok.Builder;

@Builder
public record Seat(
    long id,
    SeatStatus status,
    int seatNumber,
    SeatOption seatOption,
    Concert concert
) {
}
