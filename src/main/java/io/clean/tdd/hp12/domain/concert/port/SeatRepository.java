package io.clean.tdd.hp12.domain.concert.port;

import io.clean.tdd.hp12.domain.concert.model.Seat;

import java.util.List;

public interface SeatRepository {
    List<Seat> findByConcertId(long concertId);

    Seat findByConcertIdAndSeatNumber(long concertId, int seatNumber);

    Seat save(Seat seat);
}
