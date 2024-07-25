package io.clean.tdd.hp12.domain.concert.model;

import lombok.Builder;

@Builder
public record SeatOption(
    long id,
    String classifiedAs,
    long price
) {
}
