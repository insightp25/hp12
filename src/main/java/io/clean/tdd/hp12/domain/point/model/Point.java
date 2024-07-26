package io.clean.tdd.hp12.domain.point.model;

import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Point(
    long id,
    long point,
    LocalDateTime updatedAt,
    User user
) {
}
