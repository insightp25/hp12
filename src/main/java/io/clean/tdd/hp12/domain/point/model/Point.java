package io.clean.tdd.hp12.domain.point.model;

import io.clean.tdd.hp12.domain.common.CustomException;
import io.clean.tdd.hp12.domain.common.ErrorCode;
import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Builder
public record Point(
    long id,
    long point,
    LocalDateTime updatedAt,
    User user
) {
    public Point charge(long amount) {
        return Point.builder()
            .id(id)
            .point(point + amount)
            .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }

    public Point use(long amount) {
        return Point.builder()
            .id(id)
            .point(point - amount)
            .updatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }

    public static void validate(long amount) {
        if (amount <= 0) {
            throw new CustomException(ErrorCode.BAD_INPUT_POINT_VALUE_ERROR);
        }
    }

    public void validateSufficient(long amount) {
        if (point < amount) {
            throw new CustomException(ErrorCode.INSUFFICIENT_POINTS_ERROR);
        }
    }
}
