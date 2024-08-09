package io.clean.tdd.hp12.domain.reservation.model;

import io.clean.tdd.hp12.domain.reservation.enums.PaymentStatus;
import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record Payment(
    long id,
    long amount,
    PaymentStatus status,
    LocalDateTime createdAt,
    User user
) {
}
