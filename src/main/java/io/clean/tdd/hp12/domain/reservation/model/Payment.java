package io.clean.tdd.hp12.domain.reservation.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Payment(
    long id,
    LocalDateTime createdAt,
    Reservation reservation
) {
}
