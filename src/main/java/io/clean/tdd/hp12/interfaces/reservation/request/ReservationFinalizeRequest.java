package io.clean.tdd.hp12.interfaces.reservation.request;

public record ReservationFinalizeRequest(
    long userId,
    long paymentId
) {
}
