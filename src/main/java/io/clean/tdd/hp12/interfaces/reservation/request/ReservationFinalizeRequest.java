package io.clean.tdd.hp12.interfaces.reservation.request;

import lombok.Builder;

@Builder
public record ReservationFinalizeRequest(
    long userId,
    long paymentId
) {
}
