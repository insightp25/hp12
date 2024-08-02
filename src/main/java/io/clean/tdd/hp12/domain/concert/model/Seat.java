package io.clean.tdd.hp12.domain.concert.model;

import io.clean.tdd.hp12.domain.common.CustomException;
import io.clean.tdd.hp12.domain.common.ErrorCode;
import io.clean.tdd.hp12.domain.concert.enums.SeatStatus;
import lombok.Builder;

@Builder
public record Seat(
    long id,
    SeatStatus status,
    int seatNumber,
    SeatOption seatOption,
    Concert concert
) {
    public void validateAvailabile() {
        if (status.equals(SeatStatus.OCCUPIED)) {
            throw new CustomException(ErrorCode.SEAT_OCCUPIED_ERROR, "좌석 %d 번".formatted(seatNumber()));
        }
    }

    public Seat hold() {
        return io.clean.tdd.hp12.domain.concert.model.Seat.builder()
            .id(id)
            .status(SeatStatus.ON_HOLD)
            .seatNumber(seatNumber)
            .seatOption(seatOption)
            .concert(concert)
            .build();
    }
}
