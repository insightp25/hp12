package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.domain.concert.model.Seat;
import io.clean.tdd.hp12.domain.concert.port.SeatRepository;
import io.clean.tdd.hp12.infrastructure.concert.entity.SeatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository seatJpaRepository;

    @Override
    public List<Seat> findByConcertId(long concertId) {
        return seatJpaRepository.findByConcertEntity_Id(concertId).stream()
            .map(SeatEntity::toModel)
            .toList();
    }

    @Override
    public Seat findByConcertIdAndSeatNumber(long concertId, int seatNumber) {
        return seatJpaRepository.findByConcertEntity_IdAndSeatNumber(concertId, seatNumber)
            .toModel();
    }

    @Override
    public Seat save(Seat seat) {
        return seatJpaRepository.save(SeatEntity.from(seat))
            .toModel();
    }
}
