package io.clean.tdd.hp12.domain.point.model;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Point(
    long id,
    long point,
    LocalDateTime createdAt
) {
}
