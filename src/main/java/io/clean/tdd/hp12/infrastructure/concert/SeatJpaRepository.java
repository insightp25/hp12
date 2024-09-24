package io.clean.tdd.hp12.infrastructure.concert;

import io.clean.tdd.hp12.infrastructure.concert.entity.SeatEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Lock;

public interface SeatJpaRepository extends JpaRepository<SeatEntity, Long> {
    List<SeatEntity> findByConcertEntity_Id(long concertId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    SeatEntity findByConcertEntity_IdAndSeatNumber(long concertId, int seatNumber);
}
