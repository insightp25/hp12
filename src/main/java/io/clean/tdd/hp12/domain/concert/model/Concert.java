package io.clean.tdd.hp12.domain.concert.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Concert(
    long id,
    LocalDateTime occasion,
    ConcertTitle concertTitle
) {
}
