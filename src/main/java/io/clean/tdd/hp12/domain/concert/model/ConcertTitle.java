package io.clean.tdd.hp12.domain.concert.model;

import lombok.Builder;

@Builder
public record ConcertTitle(
    long id,
    String title,
    String description
) {
}
