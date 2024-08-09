package io.clean.tdd.hp12.interfaces.reservation.request;

import lombok.Builder;

import java.util.List;

@Builder
public record ReservationHoldRequest(
    long userId,
    long concertId,
    List<Integer> seatNumbers
) {

}
