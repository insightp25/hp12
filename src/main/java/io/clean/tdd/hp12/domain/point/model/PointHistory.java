package io.clean.tdd.hp12.domain.point.model;

import io.clean.tdd.hp12.domain.point.enums.TransactionType;
import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PointHistory(
    long id,
    long amount,
    TransactionType type,
    LocalDateTime updatedAt,
    User user
) {
}
