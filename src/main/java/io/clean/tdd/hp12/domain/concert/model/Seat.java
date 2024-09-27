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
    Concert concert,

    int version // 낙관락 위한 JPA 연관 로직 예외적 추가
) {
    public void validateAvailabile() {
        if (status.equals(SeatStatus.ON_HOLD) || status.equals(SeatStatus.OCCUPIED)) {
            throw new CustomException(ErrorCode.SEAT_OCCUPIED_ERROR, "좌석 %d 번".formatted(seatNumber()));
        }
    }

    public Seat hold() {
        return Seat.builder()
            .id(id)
            .status(SeatStatus.ON_HOLD)
            .seatNumber(seatNumber)
            .seatOption(seatOption)
            .concert(concert)
            .version(version) // 낙관락 위한 JPA 연관 로직 예외적 추가
            .build();
    }

    public Seat close() {
        return Seat.builder()
            .id(id)
            .status(SeatStatus.OCCUPIED)
            .seatNumber(seatNumber)
            .seatOption(seatOption)
            .concert(concert)
            .version(version) // 낙관락 위한 JPA 연관 로직 예외적 추가
            .build();
    }

    public Seat vacate() {
        return Seat.builder()
            .id(id)
            .status(SeatStatus.AVAILABLE)
            .seatNumber(seatNumber)
            .seatOption(seatOption)
            .concert(concert)
            .version(version) // 낙관락 위한 JPA 연관 로직 예외적 추가
            .build();
    }
}
