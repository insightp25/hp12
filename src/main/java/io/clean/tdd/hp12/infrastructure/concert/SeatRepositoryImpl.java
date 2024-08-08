package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Seat> findByConcertId(long concertId) {
        return seatJpaRepository.findByConcert_Id(concertId);
    }

    @Override
    public Seat findByConcertIdAndSeatNumber(long concertId, int seatNumber) {
        return seatJpaRepository.findByConcert_IdAndSeatNumber(concertId, seatNumber);
    }

    @Override
    public Seat save(Seat seat) {
        return seatJpaRepository.save(seat);
    }
}
