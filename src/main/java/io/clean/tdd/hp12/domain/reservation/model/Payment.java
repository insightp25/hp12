package io.clean.tdd.hp12.domain.reservation.model;

import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.reservation.enums.PaymentStatus;
import io.clean.tdd.hp12.domain.user.model.User;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Builder
public record Payment(
    long id,
    long amount,
    PaymentStatus status,
    LocalDateTime createdAt,
    User user
) {
    public static Payment issuePayment(User user, long amount) {
        return Payment.builder()
            .amount(amount)
            .status(PaymentStatus.PENDING)
            .createdAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .user(user)
            .build();
    }

    public static long calculateAmount(List<Seat> seats) {
        return seats.stream()
            .mapToLong(seat -> seat.seatOption().price())  // Extract prices
            .sum();
    }

    public Payment abolish() {
        return Payment.builder()
            .id(id)
            .amount(amount)
            .status(PaymentStatus.ABOLISHED)
            .createdAt(createdAt)
            .user(user)
            .build();
    }

    public Payment complete() {
        return Payment.builder()
            .id(id)
            .amount(amount)
            .status(PaymentStatus.COMPLETE)
            .createdAt(createdAt)
            .user(user)
            .build();
    }
}
