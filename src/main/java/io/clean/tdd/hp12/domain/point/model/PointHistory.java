package io.clean.tdd.hp12.domain.point.model;

import io.clean.tdd.hp12.domain.point.enums.TransactionType;
import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Builder
public record PointHistory(
    long id,
    long amount,
    TransactionType type,
    LocalDateTime updatedAt,
    User user
) {
    public static PointHistory generateChargeTypeOf(User user, long amount) {
        return PointHistory.builder()
            .amount(amount)
            .type(TransactionType.CHARGE)
            .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }

    public static PointHistory generateUseTypeOf(User user, long amount) {
        return PointHistory.builder()
            .amount(amount)
            .type(TransactionType.USE)
            .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }
}
