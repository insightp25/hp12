package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.infrastructure.concert.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findByConcertEntity_Id(long concertId);

    SeatEntity findByConcertEntity_IdAndSeatNumber(long concertId, int seatNumber);
}
